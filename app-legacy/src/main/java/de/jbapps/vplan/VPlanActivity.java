package de.jbapps.vplan;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import de.jbamberger.jutils.NetUtils;
import de.jbamberger.jutils.ViewUtils;
import de.jbapps.vplan.data.VPlanSet;
import de.jbapps.vplan.ui.VPlanBaseData;
import de.jbapps.vplan.util.JSONParser;
import de.jbapps.vplan.util.Property;
import de.jbapps.vplan.util.VPlanAdapter;
import de.jbapps.vplan.util.VPlanProvider;


public class VPlanActivity extends AppCompatActivity implements VPlanProvider.IVPlanLoader, JSONParser.IItemsParsed {

    private static final String TAG = "VPlanActivity";

    private static final String URL_MAIL_DEVELOPER = "mailto:vplanbugreport@gmail.com";
    private static final String URL_FHG_HOME = "http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlanDay-App-FHG/808086192561672";

    private static final String STATE_SHOULD_REFRESH = "refresh";

    private final RefreshListener mRefreshListener = new RefreshListener();
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private Property mProperty;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mBackgroundProgress;
    private VPlanAdapter mListAdapter;
    private VPlanProvider mVPlanProvider;
    private JSONParser mJSONParser;
    private String gradeState;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mProperty = new Property(this);
        gradeState = mProperty.readGrade();
        mVPlanProvider = new VPlanProvider(this, this);

        setupActionBar();
        setupUI();

        mNetworkStateReceiver.netStateUpdate();
        if (mProperty.getShowGradePicker()) {
            showGradePicker();
        }


        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            //initial startup
            loadVPlan(false);
        } else {
            loadCachedVPlan();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gradeState.equals(mProperty.readGrade())) {
            //returning from settings: update title and list
            gradeState = mProperty.readGrade();
            setupActionBar();
            loadCachedVPlan();
        }
        registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkStateReceiver);
        if (mJSONParser != null) {
            mJSONParser.cancel(true);
            mJSONParser = null;
        }

    }

    private void setupUI() {
        mSwipeRefreshLayout = ViewUtils.findViewById(this, R.id.vplan_refreshlayout);
        mBackgroundProgress = ViewUtils.findViewById(this, R.id.progressBar);
        ListView list = ViewUtils.findViewById(this, R.id.vplan_list);

        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.material_blue_A700, R.color.material_blue_A400, R.color.material_blue_A200, R.color.material_blue_A100);

        mListAdapter = new VPlanAdapter(this);
        list.setAdapter(mListAdapter);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        String title = gradeState;
        if (title != null && title.equals("")) title = "Alles";//FIXME: possible bug
        if (actionBar != null) actionBar.setTitle("VPlanDay - " + title);
    }

    public void showGradePicker() {
        final List<Integer> selected = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_dialog_pick_grade)
                .setMultiChoiceItems(R.array.listGrades, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selected.add(which);
                                } else if (selected.contains(which)) {
                                    selected.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String[] list = getResources().getStringArray(R.array.listGrades);
                        StringBuilder output = new StringBuilder();
                        for (int i : selected) {
                            output.append(list[i]);
                            output.append(",");
                        }
                        String grades = output.toString();
                        if (grades.length() > 0) {
                            grades = grades.substring(0, grades.length() - 1);
                        } else {
                            grades = "";
                        }
                        mProperty.storeGrade(grades);
                        mProperty.setShowGradePicker(false);
                        gradeState = grades;
                        setupActionBar();
                        loadCachedVPlan();
                    }
                })
                .setNegativeButton(R.string.select_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mProperty.storeGrade("");
                        mProperty.setShowGradePicker(false);
                        gradeState = "";
                        setupActionBar();
                        loadCachedVPlan();
                    }
                });

        builder.create().show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHOULD_REFRESH, isListEmpty());
    }

    private boolean isListEmpty() {
        return !(mListAdapter.getCount() > 0);
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
            Log.d(TAG, "invoked force reload");
            loadVPlan(true);
            return true;
        }
        if (id == R.id.action_contact_developer) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.text_dialog_contact_developer)
                    .setItems(R.array.mail_subjects, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String subject;
                            switch (which) {
                                case 0:
                                    subject = "[VPlanDay-App] question";
                                    break;
                                case 1:
                                    subject = "[VPlanDay-App] feedback";
                                    break;
                                case 2:
                                    subject = "[VPlanDay-App] bugreport";
                                    break;
                                default:
                                    subject = "[VPlanDay-App] general";
                                    break;
                            }
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(URL_MAIL_DEVELOPER));
                            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                            startActivity(intent);
                        }
                    });
            builder.create().show();

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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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

    private void loadVPlan(boolean ignored) {
        toggleLoading(true);
        /* FIXME: impl

        FhgApi.Builder.getInstance(this).getVPlan().observe(this, new Observer<ApiResponse<VPlan>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<VPlan> response) {
                if(response == null) return;
                if (response.isSuccessful()) {
                    onItemsParsed(Transformer.transform(response.body));
                } else {
                    Log.d(TAG, response.errorMessage);
                    toggleLoading(false);
                }
            }
        });*/
    }

    private void loadCachedVPlan() {
        loadVPlan(false);
    }

    @Override
    public void vPlanLoaded(VPlanSet vplanset) {
        Log.i(TAG, "vPlanLoaded executed");
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
        Log.i(TAG, "JSON parsed");
        mListAdapter.setData(dataList);
        mListAdapter.notifyDataSetChanged();
        Log.i(TAG, "Data supplied to ListView");
        toggleLoading(false);
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
            if (NetUtils.isNetworkAvailable(mActivity)) {
                SnackbarManager.dismiss();
            } else {
                SnackbarManager.show(Snackbar.with(getApplicationContext())
                        .text(getString(R.string.text_net_disconnected))
                        .colorResource(R.color.material_red_500)
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .swipeToDismiss(false), mActivity);
            }
        }
    }
}