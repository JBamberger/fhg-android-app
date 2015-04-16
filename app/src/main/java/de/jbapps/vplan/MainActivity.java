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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
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
import java.util.concurrent.atomic.AtomicInteger;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.util.NetUtils;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanJSONParser;
import de.jbapps.vplan.util.VPlanLoader;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, VPlanLoader.IOnFinishedLoading, VPlanJSONParser.IOnFinishedLoading {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_VPLAN_ID = "vplan_id";
    private static final String TAG = "MainActivity";
    private static final String API_ADD = "http://fhg42-vplanapp.rhcloud.com/add";
    private static final String API_PING = "http://fhg42-vplanapp.rhcloud.com/ping";
    private static final String API_TRIGGER = "http://fhg42-vplanapp.rhcloud.com/trigger";
    private static final String URL_MAIL_BUG_REPORT = "mailto:vplanbugreport@gmail.com";
    private static final String URL_FHG_HOME = "http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlan-App-FHG/808086192561672";
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
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final RefreshListener mRefreshListener = new RefreshListener();
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    String regid;
    private ListView mList;
    private TextView mStatus;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;
    private boolean mOnline = false;
    private Context mContext;
    private SharedPreferences mPreferences;
    private Header[] mVPlanHeader1 = new Header[2];
    private Header[] mVPlanHeader2 = new Header[2];
    private JSONObject mVPlan1;
    private JSONObject mVPlan2;
    private VPlanLoader mVPlanLoader;
    private VPlanJSONParser mVPlanJSONParser;
    private String SENDER_ID = "913892810147";
    private GoogleCloudMessaging gcm;
    private AtomicInteger msgId = new AtomicInteger();
    private Context context;

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

    private void setupGcm() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
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

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
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

    private String getVPlanId() {
        final SharedPreferences prefs = getGcmPreferences();
        String vplanID = prefs.getString(PROPERTY_VPLAN_ID, "");
        if (vplanID.isEmpty()) {
            Log.i(TAG, "VPlanID not found.");
            return "";
        } else {
            return vplanID;
        }
    }

    private void storeVPlanId(String vplanId) {
        final SharedPreferences prefs = getGcmPreferences();
        Log.i(TAG, "Saving vplanId");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_VPLAN_ID, vplanId);
        editor.commit();
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
                    storeRegistrationId(context, regid);
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO notify user
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }
        }.execute(null, null, null);
    }

    /**
     * FIXME: NEVER USED...
     * */
    public void sendGcmMessage(String head) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("my_message", "Hello World");
                    data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    private SharedPreferences getGcmPreferences() {
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void sendRegistrationIdToBackend(String regId) {
        List<NameValuePair> nvp = new ArrayList<>();
        nvp.add(new BasicNameValuePair("gcm", regId));
        try {
            String response = doPost(API_ADD, nvp);
            Log.d(TAG, "Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    Log.d(TAG, "Trigger Response: " + content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(connection != null) connection.disconnect();
                }
                return null;
            }
        }.execute();



    }

    private void doPing(final String id) {

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
                    Log.d(TAG, "Ping Response: " + content);
                    //TODO: use content
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
        Log.d(TAG, gcmId);
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
                    Log.d(TAG, "Add Response: " + content);
                    JSONObject json = new JSONObject(content);
                    switch(Integer.parseInt(json.getString("status"))) {
                        case 0:
                            Log.e(TAG,"Adding failed: " + json.getString("error"));
                            break;
                        case 1:
                            String vId = json.getString("insert");
                            storeVPlanId(vId);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

    //TODO async
    private String doPost(String URL, List<NameValuePair> nameValuePairs) throws IOException {
        HttpURLConnection mConnection = getDefaultURLConnection(URL);
        OutputStream os = mConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(NetUtils.getQuery(nameValuePairs));
        writer.flush();
        writer.close();
        os.close();
        mConnection.connect();
        InputStream in = mConnection.getInputStream();
        return IOUtils.toString(in, "UTF-8");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        context = getApplicationContext();

        mStatus = (TextView) findViewById(R.id.status);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vplan_refreshlayout);
        mBackgroundProgress = (ProgressBar) findViewById(R.id.progressBar);
        mList = (ListView) findViewById(R.id.vplan_list);

        setupActionBar();
        setupSwipeRefreshLayout();
        setupListView();
        setupGcm();

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
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(URL_MAIL_BUG_REPORT));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Bugreport");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(URL_MAIL_BUG_REPORT));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[VPlan-App] Feedback/Frage");
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_classic_view) {
            Intent intent = new Intent(this, VPlanActivity.class);
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_FHG_HOME));
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_vplan_hp) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_VPLAN_HOME));
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


        if (id == R.id.action_add) {
            doAdd(getRegistrationId(context));
            return true;
        }
        if (id == R.id.action_trigger) {
            doTrigger();
            return true;
        }
        if (id == R.id.action_ping) {
            doPing(getVPlanId());//TODO
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
        if (mVPlan1 != null && mVPlan2 != null) {
            mVPlanJSONParser = new VPlanJSONParser(this, readGrade(), mVPlan1, mVPlan2);
            mVPlanJSONParser.execute();
        }
    }

    @Override
    public synchronized void onVPlanHeaderLoaded(Header[] vPlanHeader1, Header[] vPlanHeader2) {
        try {
            readVPlanHeader();
            if (mVPlanHeader1[0].getValue().equals(vPlanHeader1[0].getValue()) && mVPlanHeader2[0].getValue().equals(vPlanHeader2[0].getValue())) {
                invokeVPlanCacheRestore(false);
                return;
            }
            invokeVPlanDownload(false);
            //TODO: ping server with vPlanHeader[0]
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
