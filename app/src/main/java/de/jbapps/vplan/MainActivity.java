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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanJSONParser;
import de.jbapps.vplan.util.VPlanLoader;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, VPlanLoader.IOnFinishedLoading, VPlanJSONParser.IOnFinishedLoading {

    private static final String TAG = "MainActivity";

    private static final String STATE_SHOULD_REFRESH = "refresh";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final String PREFS = "vplan_preferences";
    private static final String PREFS_CGRADE = "selected_grade";
    private static final String PREFS_KEY_VPLAN_HEADER_1 = "vplan_header_1";
    private static final String PREFS_KEY_VPLAN_HEADER_2 = "vplan_header_2";
    private static final String PREFS_KEY_VPLAN_CONTENT_1 = "vplan_content_1";
    private static final String PREFS_KEY_VPLAN_CONTENT_2 = "vplan_content_2";

    private static final String JSON_KEY_HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String JSON_KEY_HEADER_CONTENT_LENGTH = "Content-Length";

    private ListView mList;
    private TextView mStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;

    private boolean mOnline = false;
    private final RefreshListener mRefreshListener = new RefreshListener();
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private Context mContext;
    private SharedPreferences mPreferences;

    private Header[] mVPlanHeader1 = new Header[2];
    private Header[] mVPlanHeader2 = new Header[2];
    private JSONObject mVPlan1;
    private JSONObject mVPlan2;

    private VPlanLoader mVPlanLoader;
    private VPlanJSONParser mVPlanJSONParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        mStatus = (TextView) findViewById(R.id.status);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mBackgroundProgress = (ProgressBar) findViewById(R.id.progressBar);
        mList = (ListView) findViewById(R.id.vplan_list);

        setupActionBar();
        setupSwipeRefreshLayout();
        setupListView();

        mNetworkStateReceiver.netStateUpdate();

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            reload();
        } else {
            restore(false);
        }
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
        if (mVPlanJSONParser != null) {
            mVPlanJSONParser.cancel(true);
            mVPlanJSONParser = null;
        }
        if (mVPlanLoader != null) {
            mVPlanLoader.cancel(true);
            mVPlanLoader = null;
        }
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
        actionBar.setSelectedNavigationItem(readGradeID());
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
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mListAdapter.getCount() > 0) {
            outState.putBoolean(STATE_SHOULD_REFRESH, false);
        }
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

        if (id == R.id.action_force_reload) {
            forceReload();
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
        writeGrade(position);
        restore(false);
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

    private void reload() {
        toggleLoading(true);
        invokeVPlanDownload(true);
    }

    private void forceReload() {
        toggleLoading(true);
        invokeVPlanDownload(false);
    }

    private void restore(boolean notifyUser) {
        toggleLoading(true);
        invokeVPlanCacheRestore(notifyUser);
    }

    private void readVPlanHeader() throws JSONException {
            JSONObject h1 = new JSONObject(mPreferences.getString(PREFS_KEY_VPLAN_HEADER_1, "{}"));
            JSONObject h2 = new JSONObject(mPreferences.getString(PREFS_KEY_VPLAN_HEADER_2, "{}"));
            mVPlanHeader1[0] = new BasicHeader(JSON_KEY_HEADER_LAST_MODIFIED, h1.getString(JSON_KEY_HEADER_LAST_MODIFIED));
            mVPlanHeader1[1] = new BasicHeader(JSON_KEY_HEADER_CONTENT_LENGTH, h1.getString(JSON_KEY_HEADER_CONTENT_LENGTH));
            mVPlanHeader2[0] = new BasicHeader(JSON_KEY_HEADER_LAST_MODIFIED, h2.getString(JSON_KEY_HEADER_LAST_MODIFIED));
            mVPlanHeader2[1] = new BasicHeader(JSON_KEY_HEADER_CONTENT_LENGTH, h2.getString(JSON_KEY_HEADER_CONTENT_LENGTH));
    }

    private void writeVPlanHeader() throws JSONException {
        SharedPreferences.Editor editor = mPreferences.edit();

        JSONObject h1 = new JSONObject();
        h1.put(JSON_KEY_HEADER_LAST_MODIFIED, mVPlanHeader1[0].getValue());
        h1.put(JSON_KEY_HEADER_CONTENT_LENGTH, mVPlanHeader1[1].getValue());
        JSONObject h2 = new JSONObject();
        h2.put(JSON_KEY_HEADER_LAST_MODIFIED, mVPlanHeader2[0].getValue());
        h2.put(JSON_KEY_HEADER_CONTENT_LENGTH, mVPlanHeader2[1].getValue());

        editor.putString(PREFS_KEY_VPLAN_HEADER_1, h1.toString());
        editor.putString(PREFS_KEY_VPLAN_HEADER_2, h2.toString());
        editor.apply();
    }

    private void readVPlanContent() throws JSONException {
        mVPlan1 = new JSONObject(mPreferences.getString(PREFS_KEY_VPLAN_CONTENT_1, "{}"));
        mVPlan2 = new JSONObject(mPreferences.getString(PREFS_KEY_VPLAN_CONTENT_2, "{}"));
    }

    private void writeVPlanContent() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFS_KEY_VPLAN_CONTENT_1, mVPlan1.toString());
        editor.putString(PREFS_KEY_VPLAN_CONTENT_2, mVPlan2.toString());
        editor.apply();
    }

    private String readGrade() {
        return getResources().getStringArray(R.array.listGradePatterns)[getSupportActionBar().getSelectedNavigationIndex()];
    }

    private int readGradeID() {
        return mPreferences.getInt(PREFS_CGRADE, 0);
    }

    private void writeGrade(int position) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(PREFS_CGRADE, position);
        editor.apply();
    }

    private void invokeVPlanDownload(boolean onlyHeader) {
        if (mOnline) {
            if (mVPlanLoader != null) {
                mVPlanLoader.cancel(true);
                mVPlanLoader = null;
            }
            mVPlanLoader = new VPlanLoader(this);
            mVPlanLoader.execute(onlyHeader);

        } else {
            if (mListAdapter.getCount() == 0) {
                invokeVPlanCacheRestore(true);
            } else {
                toggleLoading(false);
                Toast.makeText(this, "Internetverbindung fehlt.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void invokeVPlanCacheRestore(boolean notifyUser) {
        try {
            readVPlanHeader();
            readVPlanContent();
            if (mVPlan1 != null || mVPlan2 != null || mVPlanHeader1[0] != null || mVPlanHeader2[0] != null) {
                onVPlanLoaded(mVPlan1, mVPlan2, mVPlanHeader1, mVPlanHeader2);
                if (notifyUser) {
                    Toast.makeText(mContext, "vPlan aus Cache wiederhergestellt.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, "Keine lokale Kopie vorhanden.", Toast.LENGTH_LONG).show();
                toggleLoading(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Fehler beim Verarbeiten der lokalen Kopie.", Toast.LENGTH_LONG).show();
            toggleLoading(false);
        }
    }

    private void invokeJSONParser() {
        if (mVPlanJSONParser != null) {
            mVPlanJSONParser.cancel(true);
            mVPlanJSONParser = null;
        }
        if (mVPlan1 != null || mVPlan2 != null) {
            mVPlanJSONParser = new VPlanJSONParser(this, readGrade(), mVPlan1, mVPlan2);
            mVPlanJSONParser.execute();
        }
    }

    @Override
    public synchronized void onVPlanHeaderLoaded(Header[] vPlanHeader1, Header[] vPlanHeader2) {
        try {
            readVPlanHeader();
            if (mVPlanHeader1[0].getValue().equals(vPlanHeader1[0].getValue()) && mVPlanHeader2[0].getValue().equals(vPlanHeader2[0].getValue())) {
                if (mVPlanHeader1[1].getValue().equals(vPlanHeader1[1].getValue()) && mVPlanHeader2[1].getValue().equals(vPlanHeader2[1].getValue())) {
                    invokeVPlanCacheRestore(false);
                    return;
                }
            }
            invokeVPlanDownload(false);
        } catch (Exception e) {
            invokeVPlanDownload(false);
        }
        Log.i(TAG, "VPlan header loading finished");
    }

    @Override
    public synchronized void onVPlanHeaderLoadingFailed() {
        Log.w(TAG, "VPlan header loading failed");
        restore(true);
    }

    @Override
    public synchronized void onVPlanLoaded(JSONObject vPlan1, JSONObject vPlan2, Header[] vPlanHeader1, Header[] vPlanHeader2) {

        mVPlanHeader1 = vPlanHeader1;
        mVPlanHeader2 = vPlanHeader2;
        mVPlan1 = vPlan1;
        mVPlan2 = vPlan2;

        invokeJSONParser();

        try {
            writeVPlanHeader();
            writeVPlanContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "VPlanLoading finished");
    }

    @Override
    public synchronized void onVPlanLoadingFailed() {
        restore(true);
        Log.w(TAG, "VPlanLoading failed");
    }

    @Override
    public void onVPlanParsed(List<VPlanBaseData> dataList) {
        applyVPlan(dataList);
        toggleLoading(false);
        Log.i(TAG, "VPlan parsed and applied");
    }

    @Override
    public void onVPlanParsingFailed() {
        Toast.makeText(mContext, "Daten konnten nicht verarbeitet werden.", Toast.LENGTH_LONG).show();
        toggleLoading(false);
        Log.w(TAG, "VPlan parsing failed");
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            reload();
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
}
