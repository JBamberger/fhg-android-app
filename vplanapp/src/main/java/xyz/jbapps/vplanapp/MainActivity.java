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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.jutils.NetUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplanapp.data.VPlanData;
import xyz.jbapps.vplanapp.ui.MultiVPlanAdapter;
import xyz.jbapps.vplanapp.util.GradeSorter;
import xyz.jbapps.vplanapp.util.Property;
import xyz.jbapps.vplanapp.util.VPlanProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VPlanApp v2";

    private static final String URL_MAIL_DEVELOPER = "mailto:vplanbugreport@gmail.com";
    private static final String URL_VPLAN_HOME = "https://www.facebook.com/pages/VPlan-App-FHG/808086192561672";
    private static final String STATE_SHOULD_REFRESH = "refresh";
    private final NetReceiver mNetworkStateReceiver = new NetReceiver();
    private final VPlanListener mVPlanListener = new VPlanListener();
    FloatingActionButton fab;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Property mProperty;
    private VPlanProvider vPlanProvider;
    private MultiVPlanAdapter multiVPlanAdapter;
    private String gradeState;
    private Activity mActivity;
    private AdView mAdView;

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
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_FORCE_LOAD);
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
            showGradePicker();
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_VPLAN_HOME)));
            return true;
        }
        if (id == R.id.action_credits) {
            startActivity(new Intent(this, CreditsActivity.class));
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


        setupUI();

        mNetworkStateReceiver.netStateUpdate();
        if (mProperty.getShowGradePicker()) {
            showGradePicker();
        }

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
        } else {
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_CACHE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (!gradeState.equals(mProperty.readGrade())) {
            //returning from settings: update title and list
            gradeState = mProperty.readGrade();
            setupActionBar();
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_CACHE);
        }
        registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setupUI() {
        mToolbar = ViewUtils.findViewById(this, R.id.toolbar);
        setSupportActionBar(mToolbar);
        setupActionBar();

        fab = ViewUtils.findViewById(this, R.id.vplan_reload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });
        fab.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_rotate_animation));

        mProgressBar = ViewUtils.findViewById(this, R.id.progressBar);
        mRecyclerView = ViewUtils.findViewById(this, R.id.vplan_list);
        mSwipeRefreshLayout = ViewUtils.findViewById(this, R.id.vplan_refreshlayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.material_blue_A700, R.color.material_blue_A400, R.color.material_blue_A200, R.color.material_blue_A100);


        multiVPlanAdapter = new MultiVPlanAdapter();
        mRecyclerView.setAdapter(multiVPlanAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.hasFixedSize();


        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        String subtitle = gradeState;
        if (subtitle != null && subtitle.equals("")) {
            subtitle = "Alles";
        }
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    public void showGradePicker() {
        GradeSorter gSorter = new GradeSorter(mProperty.readGrade());
        String[] gradeList = getResources().getStringArray(R.array.listGrades);
        boolean[] selectedItems = new boolean[gradeList.length];


        if (gSorter.matchesEverything()) {
            for (int i = gradeList.length - 1; i >= 0; i--) {
                selectedItems[i] = false;
            }
        } else {
            for (int i = gradeList.length - 1; i >= 0; i--) {
                selectedItems[i] = gSorter.matchItem(gradeList[i]);
            }
        }

        final List<Integer> selected = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.text_dialog_pick_grade)
                .setMultiChoiceItems(R.array.listGrades, selectedItems,
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
                        mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
                    }
                })
                .setNegativeButton(R.string.select_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mProperty.storeGrade("");
                        mProperty.setShowGradePicker(false);
                        gradeState = "";
                        setupActionBar();
                        mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
                    }
                });

        builder.create().show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SHOULD_REFRESH, isListEmpty());
    }

    private boolean isListEmpty() {
        return !(multiVPlanAdapter.getItemCount() > 0);
    }

    private void toggleLoading(boolean on) {
        if (on) {
            mProgressBar.setVisibility(View.VISIBLE);
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_rotate_animation));
        } else {
            mProgressBar.setVisibility(View.GONE);
            fab.clearAnimation();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private class VPlanListener implements VPlanProvider.IVPlanResultListener {

        @Override
        public void vPlanLoadingFailed() {
            toggleLoading(false);
            Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_LONG); //TODO: use resources
        }

        @Override
        public void vPlanLoadingSucceeded(VPlanData vplan1, VPlanData vplan2) {
            toggleLoading(false);
            GradeSorter gSorter = new GradeSorter(gradeState);
            vplan1 = gSorter.applyPatternToData(vplan1);
            vplan2 = gSorter.applyPatternToData(vplan2);
            multiVPlanAdapter.setData(vplan1, vplan2);
        }

        public void loadVPlan(int method) {
            toggleLoading(true);
            if (vPlanProvider != null) {
                vPlanProvider.cancel(true);
                vPlanProvider = null;
            }
            vPlanProvider = new VPlanProvider(getApplicationContext(), method, this);
            vPlanProvider.execute();
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
