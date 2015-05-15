package de.jbapps.vplan;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.io.IOException;
import java.util.List;

import de.jbapps.vplan.data.VPlanSet;
import de.jbapps.vplan.ui.VPlanBaseData;
import de.jbapps.vplan.util.API_v1;
import de.jbapps.vplan.util.JSONParser;
import de.jbapps.vplan.util.Property;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanProvider;


public class VPlanActivity extends ActionBarActivity implements VPlanProvider.IVPlanLoader, JSONParser.IItemsParsed {

    private static final String TAG = "VPlanActivity";

    private static final String URL_MAIL_DEVELOPER = "mailto:vplanbugreport@gmail.com";
    private static final String URL_FHG_HOME = "http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlan-App-FHG/808086192561672";

    private static final String STATE_SHOULD_REFRESH = "refresh";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "913892810147";

    private final RefreshListener mRefreshListener = new RefreshListener();
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private final Property mProperty = new Property(this);
    private final API_v1 mAPI = new API_v1();

    private String regid;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;
    private VPlanProvider mVPlanProvider;
    private JSONParser mJSONParser;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String gradeState;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        context = getApplicationContext();
        gradeState = mProperty.readGrade();
        mVPlanProvider = new VPlanProvider(this, this);

        setupActionBar();
        setupUI();
        setupGcm();

        mNetworkStateReceiver.netStateUpdate();

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            loadVPlan(false);
        } else {
            loadCachedVPlan();
        }
        mAPI.doPing(mProperty.getClientId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gradeState.equals(mProperty.readGrade())) {
            gradeState = mProperty.readGrade();
            setupActionBar();
            loadCachedVPlan();
        }
        checkPlayServices();
        IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, mNetworkStateFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
        if (mJSONParser != null) {
            mJSONParser.cancel(true);
            mJSONParser = null;
        }
        mVPlanProvider.cancel();

    }

    private void setupUI() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mBackgroundProgress = (ProgressBar) findViewById(R.id.progressBar);
        ListView list = (ListView) findViewById(R.id.vplan_list);

        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.material_blue_A700, R.color.material_blue_A400, R.color.material_blue_A200, R.color.material_blue_A100);

        mListAdapter = new VPlanAdapter(this);
        list.setAdapter(mListAdapter);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        String title = gradeState;
        if (title != null && title.equals("")) title = "Alles";
        actionBar.setTitle("VPlan - " + title);
    }

    private void setupGcm() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = mProperty.getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mListAdapter.getCount() > 0) {
            outState.putBoolean(STATE_SHOULD_REFRESH, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_force_reload) {
            Log.d(TAG, "FORCE RELOAD");
            loadVPlan(true);
            return true;
        }
        if (id == R.id.action_bug) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(URL_MAIL_DEVELOPER));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Bugreport");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(URL_MAIL_DEVELOPER));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Feedback/Frage");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_classic_view) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_FHG_HOME)));
            return true;
        }
        if (id == R.id.action_vplan_hp) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_VPLAN_HOME)));
            return true;
        }
        if (id == R.id.action_credits) {
            startActivity(new Intent(this, CreditsActivity.class));
            return true;
        }
        if (id == R.id.action_get_version) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
                Toast.makeText(this, "Version " + info.versionName, Toast.LENGTH_LONG).show();
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "Fehler im Packagename!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_add) {
            mAPI.doAdd(mProperty.getRegistrationId(context), mProperty);
            return true;
        }
        if (id == R.id.action_trigger) {
            mAPI.doTrigger();
            return true;
        }
        if (id == R.id.action_ping) {
            mAPI.doPing(mProperty.getClientId());
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void toggleLoading(boolean on) {
        if (mSwipeRefreshLayout.isRefreshing() != on) {
            mSwipeRefreshLayout.setRefreshing(on);
        }
        if (on) {
            if (mListAdapter.getCount() == 0) {
                mBackgroundProgress.setVisibility(View.VISIBLE);
            }
        } else {
            mBackgroundProgress.setVisibility(View.GONE);
        }
    }

    private void loadVPlan(boolean forceLoad) {
        toggleLoading(true);
        mVPlanProvider.getVPlan(forceLoad);
    }

    private void loadCachedVPlan() {
        toggleLoading(true);
        mVPlanProvider.getCachedVPlan();
    }

    @Override
    public void vPlanLoaded(VPlanSet vplanset) {
        if (vplanset != null) {
            if (mJSONParser != null) {
                mJSONParser.cancel(true);
                mJSONParser = null;
            }
            mJSONParser = new JSONParser(this, mProperty.readGrade(), vplanset);
            mJSONParser.execute();
        } else {
            Toast.makeText(this, getString(R.string.text_no_vplan), Toast.LENGTH_LONG).show();
            toggleLoading(false);
        }
    }

    @Override
    public void onItemsParsed(List<VPlanBaseData> dataList) {
        mListAdapter.setData(dataList);
        toggleLoading(false);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.i(TAG, "Device registered, registration ID=" + regid);

                    sendRegistrationIdToBackend(regid);
                    mProperty.storeRegistrationId(context, regid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String regId) {
        mAPI.doAdd(regId, mProperty);
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadVPlan(false);
        }
    }

    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netStateUpdate();
        }

        public void netStateUpdate() {
            if (isOnline()) {
                SnackbarManager.dismiss();
            } else {
                SnackbarManager.show(Snackbar.with(getApplicationContext())
                        .text(getString(R.string.text_net_disconnected))
                        .colorResource(R.color.material_red_500)
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .swipeToDismiss(false), mActivity);
            }
        }

        public boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }
}
