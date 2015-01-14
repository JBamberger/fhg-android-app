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
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.jbapps.vplan.data.VPlan;
import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanHTJParser;
import de.jbapps.vplan.util.VPlanHeaderLoader;
import de.jbapps.vplan.util.VPlanJSONParser;
import de.jbapps.vplan.util.VPlanLoader;
import de.jbapps.vplan.util.VPlanParser;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, VPlanLoader.IOnFinishedLoading, VPlanParser.IOnFinishedLoading {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String PREFS = "vplan_preferences";
    private static final String PREFS_CGRADE = "selected_grade";

    private ListView mList;
    private TextView mStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VPlanAdapter mListAdapter;
    private VPlanLoader mLoader;
    private VPlanParser mParser;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String vplan1;
    private String vplan2;
    private boolean mOnline = false;
    private RefreshListener mRefreshListener = new RefreshListener();
    private NetReceiver mNetworkStateReceiver = new NetReceiver();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this;
        mPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        mStatus = (TextView) findViewById(R.id.status);


        //setup ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //setup ActionBarSpinner
        mSpinnerAdapter = new ArrayAdapter<>(
                actionBar.getThemedContext(),
                R.layout.spinner_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.listGrades));
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
        actionBar.setSelectedNavigationItem(getSharedPreferences(PREFS, MODE_PRIVATE).getInt(PREFS_CGRADE, 0));

        //setup SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.green_100, R.color.green, R.color.green_100);

        //setup ListView
        mList = (ListView) findViewById(R.id.vplan_list);
        mListAdapter = new VPlanAdapter(this);
        mList.setAdapter(mListAdapter);

        mNetworkStateReceiver.netStateUpdate();

        if (savedInstanceState == null || savedInstanceState.getBoolean("refresh", true)) {
            refresh();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        vplan1 = savedInstanceState.getString("v1");
        vplan2 = savedInstanceState.getString("v2");
        parse();
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("refresh", false);
        outState.putString("v1", vplan1);
        outState.putString("v2", vplan2);
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREFS_CGRADE, position);
        editor.apply();
        //TODO: improve...
        parse();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, mNetworkStateFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
        if (mLoader != null) {
            mLoader.cancel(true);
            mLoader = null;
        }
        if (mParser != null) {
            mParser.cancel(true);
            mParser = null;
        }
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

    private String getGrade() {
        String[] patterns = getResources().getStringArray(R.array.listGradePatterns);
        return patterns[getSupportActionBar().getSelectedNavigationIndex()];
    }

    private void refresh() {
        if (mOnline) {
            mSwipeRefreshLayout.setRefreshing(true);

            new VPlanHeaderLoader().execute();

            /*if (mLoader == null) {
                mLoader = new VPlanLoader(this);
                mLoader.execute();
            }*/
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Internetverbindung fehlt.", Toast.LENGTH_LONG).show();
        }
    }

    private void parse() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (mParser == null) {
            if (vplan1 != null || vplan2 != null) {
                mParser = new VPlanParser(this, getGrade(), vplan1, vplan2);
                mParser.execute();
                //new VPlanHTJParser(vplan1, vplan2).execute();

            }
        }
    }

    @Override
    public synchronized void onVPlanLoadingFailed() {
        Toast.makeText(mContext, "Daten konnten nicht geladen werden.", Toast.LENGTH_LONG).show();
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    @Override
    public synchronized void onVPlanLoaded(String v1, String v2) {
        vplan1 = v1;
        vplan2 = v2;
        parse();
        mLoader = null;
    }

    @Override
    public synchronized void onVPlanParsed(List<VPlanBaseData> dataList) {
        mListAdapter.setData(dataList);
        mSwipeRefreshLayout.setRefreshing(false);
        mParser = null;
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    @Override
    public synchronized void onVPlanParsingFailed() {
        Toast.makeText(mContext, "Daten konnten nicht verarbeitet werden.", Toast.LENGTH_LONG).show();
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refresh();
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
                if (vplan1 == null) {
                    refresh();
                }
            } else {
                showError(getString(R.string.text_net_disconnected));
            }
        }
    }

//##################################################################################################

    private static final String PREFS_KEY_VPLAN_HEADER_1 = "vplan_header_1";
    private static final String PREFS_KEY_VPLAN_HEADER_2 = "vplan_header_2";
    private static final String PREFS_KEY_VPLAN_CONTENT_1 = "vplan_content_1";
    private static final String PREFS_KEY_VPLAN_CONTENT_2 = "vplan_content_2";


    private String mVPlanHeader1;
    private String mVPlanHeader2;
    private VPlan mVPlan1;
    private VPlan mVPlan2;
    private ProgressBar mBackgroundProgress;
    private SharedPreferences mPreferences;

    private VPlanHeaderLoader mVPlanHeaderLoader;
    private VPlanLoader mVPlanLoader;
    private VPlanHTJParser mVPlanHTJParser;
    private VPlanJSONParser mVPlanJSONParser;

    private void loadVPlanHeader() {
        mVPlanHeader1 = mPreferences.getString(PREFS_KEY_VPLAN_HEADER_1, null);
        mVPlanHeader2 = mPreferences.getString(PREFS_KEY_VPLAN_HEADER_2, null);
    }

    private void writeVPlanHeader() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFS_KEY_VPLAN_HEADER_1, mVPlanHeader1);
        editor.putString(PREFS_KEY_VPLAN_HEADER_1, mVPlanHeader2);
        editor.apply();
    }

    private void loadVPlanContent() {
        mVPlan1 = new VPlan(mPreferences.getString(PREFS_KEY_VPLAN_CONTENT_1, null));//TODO: fix constructor
        mVPlan2 = new VPlan(mPreferences.getString(PREFS_KEY_VPLAN_CONTENT_2, null));//TODO: fix constructor
    }

    private void writeVPlanContent() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFS_KEY_VPLAN_CONTENT_1, mVPlan1.toString());//TODO: fix toString()
        editor.putString(PREFS_KEY_VPLAN_CONTENT_2, mVPlan2.toString());//TODO: fix toString()
        editor.apply();
    }

    private void invokeHeaderDownload() {}

    private void invokeDownload() {}

    private void invokeHTMLParser() {}

    private void invokeJSONParser() {}











}
