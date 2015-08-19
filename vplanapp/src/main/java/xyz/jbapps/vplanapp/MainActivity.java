package xyz.jbapps.vplanapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.jutils.NetUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplanapp.data.VPlanData;
import xyz.jbapps.vplanapp.util.Property;
import xyz.jbapps.vplanapp.util.VPlanAdapter;
import xyz.jbapps.vplanapp.util.VPlanLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VPlanApp v2";

    private static final String URL_MAIL_DEVELOPER = "mailto:vplanbugreport@gmail.com";
    private static final String URL_FHG_HOME = "http://www.fhg-radolfzell.de/vertretungsplan/v_plan.htm";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlan-App-FHG/808086192561672";
    private static final int STATUS_LOADING = 0;
    private static final int STATUS_VISIBLE = 1;
    private static final int STATUS_PARSING = 2;
    private static final int STATUS_NO_NETWORK = 3;
    private static final String STATE_SHOULD_REFRESH = "refresh";
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private final VPlanListener mVPlanListener = new VPlanListener();
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private int mStatus;
    private Property mProperty;

    private VPlanAdapter mListAdapter;
    private String gradeState;
    private Activity mActivity;

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
            //TODO: loadVPlan(true);
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
                                    subject = getString(R.string.text_subject_question);
                                    break;
                                case 1:
                                    subject = getString(R.string.text_subject_feedback);
                                    break;
                                case 2:
                                    subject = getString(R.string.text_subject_bug);
                                    break;
                                default:
                                    subject = getString(R.string.text_subject_general);
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
        if (id == R.id.action_set_grade) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_VPLAN_HOME)));
            return true;
        }
        if (id == R.id.action_credits) {
            startActivity(new Intent(this, CreditsActivity.class));
            return true;
        }
        if (id == R.id.action_settings) {
            //TODO: change
            mVPlanListener.loadVPlan();
            //showGradePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mProperty = new Property(this);
        gradeState = mProperty.readGrade();

        setupActionBar();
        setupUI();

        mNetworkStateReceiver.netStateUpdate();
        if (mProperty.getShowGradePicker()) {
            showGradePicker();
        }

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            loadVPlan();
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
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
    }

    private void setupUI() {
        mToolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressBar = ViewUtils.findViewById(this, R.id.progressBar);
        mRecyclerView = ViewUtils.findViewById(this, R.id.vplan_list);

        mListAdapter = new VPlanAdapter(this);
        //TODO: rewrite adapter for recyclerview mRecyclerView.setAdapter(mListAdapter);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        String title = gradeState;
        if (title != null && title.equals("")) title = "Alles";//FIXME: possible bug
        if (actionBar != null) actionBar.setSubtitle("VPlan - " + title);
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
        outState.putBoolean(STATE_SHOULD_REFRESH, isListEmpty());
    }

    private boolean isListEmpty() {
        return !(mListAdapter.getCount() > 0);
    }

    private void toggleLoading(boolean on) {
        if (on) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void loadVPlan() {
        toggleLoading(true);
        //TODO: load
    }

    private void loadCachedVPlan() {
        toggleLoading(true);
        //TODO: load cache
    }


    private class VPlanListener implements VPlanLoader.IOnLoadingFinished {
        @Override
        public synchronized void loaderFinished(VPlanData vplan1, VPlanData vplan2) {
            toggleLoading(false);
        }

        public void loadVPlan() {
            toggleLoading(true);
            new VPlanLoader(this).execute();
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
