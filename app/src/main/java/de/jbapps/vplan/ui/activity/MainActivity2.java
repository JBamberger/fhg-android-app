package de.jbapps.vplan.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.jbapps.vplan.R;
import de.jbapps.vplan.VPlanService;
import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.ui.VPlanAdapter;
import de.jbapps.vplan.util.StorageManager;
import de.jbapps.vplan.util.net.VPlanUpdater;

public class MainActivity2 {//extends ActionBarActivity implements ActionBar.OnNavigationListener {
/*
    private static final String TAG = "MainActivity";

    StorageManager mStorageManager;


    private ListView mList;
    private TextView mStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;

    private boolean mOnline = false;
    private RefreshListener mRefreshListener = new RefreshListener();
    private NetReceiver mNetworkStateReceiver = new NetReceiver();
    private Context mContext;
    private SharedPreferences mPreferences;

    private VPlanUpdater mVPlanUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, VPlanService.class));

        mContext = this;
        mStorageManager = new StorageManager(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mStatus = (TextView) findViewById(R.id.status);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mBackgroundProgress = (ProgressBar) findViewById(R.id.progressBar);
        mList = (ListView) findViewById(R.id.vplan_list);

        setupActionBar();
        setupSwipeRefreshLayout();
        setupListView();

        mNetworkStateReceiver.netStateUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, mNetworkStateFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupActionBar() {
        //setup ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //setup ActionBarSpinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                actionBar.getThemedContext(),
                R.layout.spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.listGrades));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);
        actionBar.setSelectedNavigationItem(mStorageManager.readGradeIndex());
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green_100, R.color.green, R.color.green_100);
    }

    private void setupListView() {
        mListAdapter = new VPlanAdapter(this);
        mList.setAdapter(mListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_force_reload) {
            requestUpdate();
            return true;
        }
        if (id == R.id.action_bug) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vplanbugreport@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Bugreport");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vplanbugreport@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Feedback/Frage");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_classic_view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm"));
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_vplan_hp) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/VPlan-App-FHG/808086192561672"));
            startActivity(intent);
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


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        mStorageManager.writeGradeIndex(position);
        //TODO: restore(false);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void showError(String message) {
        mStatus.setText(message);
        mStatus.setBackgroundResource(R.color.red);
        mStatus.setVisibility(View.VISIBLE);
    }

    private void applyVPlan(List<VPlanBaseData> data) {
        mListAdapter.setData(data);
        toggleLoading(false);
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

//##################################################################################################

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            requestHeader();
        }
    }

    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            netStateUpdate();
        }

        public void netStateUpdate() {
            if (mOnline = isOnline()) {
                mStatus.setVisibility(View.GONE);
            } else {
                showError(getString(R.string.text_net_disconnected));
            }
        }
    }
//##################################################################################################*/
}
