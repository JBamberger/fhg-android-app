package de.jbapps.vplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.data.VPlanSet;
import de.jbapps.vplan.ui.VPlanBaseData;
import de.jbapps.vplan.util.JSONParser;
import de.jbapps.vplan.util.NetUtils;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanProvider;


public class VPlanActivity extends ActionBarActivity implements VPlanProvider.IVPlanLoader, JSONParser.IItemsParsed {

    private static final String TAG = "VPlanActivity";

    private static final String API_ADD = "http://fhg42-vplanapp.rhcloud.com/add";
    private static final String API_PING = "http://fhg42-vplanapp.rhcloud.com/ping";
    private static final String API_TRIGGER = "http://fhg42-vplanapp.rhcloud.com/trigger";

    private static final String URL_MAIL_DEVELOPER = "mailto:vplanbugreport@gmail.com";
    private static final String URL_FHG_HOME = "http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlan-App-FHG/808086192561672";

    private static final String STATE_SHOULD_REFRESH = "refresh";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "913892810147";

    private final RefreshListener mRefreshListener = new RefreshListener();
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private final Property mProperty = new Property();
    private final API_v1 mAPI = new API_v1();

    private String regid;
    private ListView mList;
    private TextView mStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;
    private VPlanProvider mVPlanProvider;
    private JSONParser mJSONParser;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String gradeState;

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf(TAG, "Could not get package name: " + e);
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mStatus = (TextView) findViewById(R.id.status);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mBackgroundProgress = (ProgressBar) findViewById(R.id.progressBar);
        mList = (ListView) findViewById(R.id.vplan_list);
        gradeState = mProperty.readGrade();
        setupActionBar();
        setupSwipeRefreshLayout();
        setupListView();
        setupGcm();

        mVPlanProvider = new VPlanProvider(this, this);
        mNetworkStateReceiver.netStateUpdate();

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            reload(false);
        } else {
            restore();
        }
        mAPI.doPing(mProperty.getClientId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gradeState.equals(mProperty.readGrade())) {
            setupActionBar();
            restore();
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

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        String title = mProperty.readGrade();
        if (title != null && title.equals("")) title = "Alles";
        actionBar.setTitle("VPlan - " + title);
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green_100, R.color.green, R.color.green_100);
    }

    private void setupListView() {
        mListAdapter = new VPlanAdapter(this);
        mList.setAdapter(mListAdapter);
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
            reload(true);
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
            mAPI.doAdd(mProperty.getRegistrationId(context));
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

    private void showError(String message) {
        mStatus.setText(message);
        mStatus.setBackgroundResource(R.color.red);
        mStatus.setVisibility(View.VISIBLE);
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

    private void reload(boolean forceLoad) {
        toggleLoading(true);
        mVPlanProvider.getVPlan(forceLoad);
    }

    private void restore() {
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
        mAPI.doAdd(regId);
    }

    /**
     * This class represents the API in version 1.0
     * It contains the corresponding methods to 'add', 'ping' and 'trigger'
     */
    private class API_v1 {
        private void doTrigger() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    HttpURLConnection connection = null;
                    try {
                        connection = getDefaultURLConnection(API_TRIGGER);
                        connection.connect();
                        InputStream in = connection.getInputStream();
                        String content = IOUtils.toString(in, "UTF-8");
                        Log.i(TAG, "Trigger Response: " + content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) connection.disconnect();
                    }
                    return null;
                }
            }.execute();


        }

        private void doPing(final String id) {
            Log.i(TAG, "Pinging with id: " + id);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    HttpURLConnection connection = null;
                    try {
                        connection = getDefaultURLConnection(API_PING);

                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("id", id));

                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(NetUtils.getQuery(nameValuePairs));
                        writer.flush();
                        writer.close();
                        os.close();
                        connection.connect();
                        InputStream in = connection.getInputStream();
                        String content = IOUtils.toString(in, "UTF-8");
                        Log.i(TAG, "Ping Response: " + content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                    return null;
                }
            }.execute();
        }

        private void doAdd(final String gcmId) {
            Log.i(TAG, "Adding gcmId: " + gcmId);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    HttpURLConnection connection = null;
                    try {
                        connection = getDefaultURLConnection(API_ADD);

                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("gcm", gcmId));

                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(NetUtils.getQuery(nameValuePairs));
                        writer.flush();
                        writer.close();
                        os.close();
                        connection.connect();
                        InputStream in = connection.getInputStream();
                        String content = IOUtils.toString(in, "UTF-8");
                        Log.i(TAG, "Add Response: " + content);
                        JSONObject json = new JSONObject(content);
                        switch (Integer.parseInt(json.getString("status"))) {
                            case 0:
                                Log.e(TAG, "Adding failed: " + json.getString("error"));
                                break;
                            case 1:
                                String vId = json.getString("insert");
                                mProperty.storeClientId(vId);
                                break;
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                    return null;
                }
            }.execute();
        }

        private HttpURLConnection getDefaultURLConnection(String URL) throws IOException {
            HttpURLConnection connection;
            URL address = new URL(URL);
            connection = (HttpURLConnection) address.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            return connection;
        }
    }

    /**
     * This class provides access to the applications SharedPreferences
     */
    private class Property {

        private static final String PROPERTY_GRADES = "grades";
        private static final String PROPERTY_REG_ID = "registration_id";
        private static final String PROPERTY_CLIENT_ID = "client_id";
        private static final String PROPERTY_APP_VERSION = "appVersion";

        /**
         * These preferences belong to the MainActivity, in this case VPlanActivity
         */
        private SharedPreferences getMainPreferences() {
            return getSharedPreferences(VPlanActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        }

        /**
         * These preferences belong to the Application and represent the values stored via Settings-API
         */
        private SharedPreferences getSettingsPreferences() {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getMainPreferences();
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.apply();
        }

        private String getRegistrationId(Context context) {
            final SharedPreferences prefs = getMainPreferences();
            String registrationId = prefs.getString(PROPERTY_REG_ID, "");
            if (registrationId.isEmpty()) {
                Log.i(TAG, "Registration not found.");
                return "";
            }
            int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {
                Log.i(TAG, "App version changed.");
                return "";
            }
            return registrationId;
        }

        private String getClientId() {
            final SharedPreferences prefs = getMainPreferences();
            String vplanID = prefs.getString(PROPERTY_CLIENT_ID, "");
            if (vplanID.isEmpty()) {
                Log.i(TAG, "VPlanID not found.");
                return "";
            } else {
                return vplanID;
            }
        }

        private void storeClientId(String vplanId) {
            final SharedPreferences prefs = getMainPreferences();
            Log.i(TAG, "Saving vplanId");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_CLIENT_ID, vplanId);
            editor.apply();
        }

        /**
         * This method retrieves the 'grades' value stored in the SettingsActivity
         */
        private String readGrade() {
            return getSettingsPreferences().getString(PROPERTY_GRADES, "");
        }
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            reload(false);
        }
    }

    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netStateUpdate();
        }

        public void netStateUpdate() {
            if (isOnline()) {
                mStatus.setVisibility(View.GONE);
            } else {
                showError(getString(R.string.text_net_disconnected));
            }
        }

        public boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }
}
