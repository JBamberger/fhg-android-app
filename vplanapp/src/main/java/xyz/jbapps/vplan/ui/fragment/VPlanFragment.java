package xyz.jbapps.vplan.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.ui.MultiVPlanAdapter;
import xyz.jbapps.vplan.util.Property;
import xyz.jbapps.vplan.util.VPlanProvider;
import xyz.jbapps.vplan.util.VPlanSorter;

public class VPlanFragment extends LoadingRecyclerViewFragment implements VPlanProvider.IVPlanResultListener {

    private static final String TAG = "VPlanFragment";
    private static final String STATE_SHOULD_REFRESH = "refresh";

    private Property mProperty;
    private VPlanProvider vPlanProvider;
    private MultiVPlanAdapter multiVPlanAdapter;

    private boolean showAll;
    private String gradeState;
    private String courseState;
    private Context context;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vplan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_force_reload) {
            loadVPlan(VPlanProvider.TYPE_FORCE_LOAD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setActionBarTitle(R.string.title_fragment_vplan);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });
        multiVPlanAdapter = new MultiVPlanAdapter(context);
        recyclerView.setAdapter(multiVPlanAdapter);

        if (savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            loadVPlan(VPlanProvider.TYPE_LOAD);
        } else {
            loadVPlan(VPlanProvider.TYPE_CACHE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        mProperty = new Property(context);
        loadParserValues();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mProperty.getShowSettings()) {
            showSettings();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String grade = gradeState;
        String course = courseState;
        boolean all = showAll;
        loadParserValues();
        if (!gradeState.equals(grade) || !courseState.equals(course) || showAll != all) {
            loadVPlan(VPlanProvider.TYPE_CACHE);
        }
        String subtitle = gradeState;
        if (subtitle != null && subtitle.equals("")) {
            subtitle = getString(R.string.text_showing_all);
        }
        setActionBarSubtitle(subtitle);
    }

    private void loadParserValues() {
        showAll = mProperty.readShowAll();
        if (showAll) {
            gradeState = "";
            courseState = "";
        } else {
            gradeState = mProperty.readGrades();
            courseState = mProperty.readCourse();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SHOULD_REFRESH, !(multiVPlanAdapter.getItemCount() > 0));
    }

    @Override
    public void vPlanLoadingFailed() {
        toggleLoading(false);
        Toast.makeText(context, "Loading failed", Toast.LENGTH_LONG).show(); // TODO: use resources
    }

    @Override
    public void vPlanLoadingSucceeded(VPlanData vplan1, VPlanData vplan2) {
        toggleLoading(false);
        VPlanSorter sorter = new VPlanSorter(gradeState, courseState);
        vplan1 = sorter.filterData(vplan1);
        vplan2 = sorter.filterData(vplan2);
        multiVPlanAdapter.setData(vplan1, vplan2);
    }

    public void loadVPlan(int method) {
        updateNetworkFlags();
        if (!wifiConnected && !mobileConnected) {
            toggleLoading(false);
            Toast.makeText(context, R.string.text_network_disconnected, Toast.LENGTH_LONG).show();
            return;
        }
        if (method == VPlanProvider.TYPE_LOAD && wifiConnected) {
            method = VPlanProvider.TYPE_FORCE_LOAD;
        }
        toggleLoading(true);
        if (vPlanProvider != null) {
            vPlanProvider.cancel(true);
            vPlanProvider = null;
        }
        vPlanProvider = new VPlanProvider(context, method, this);
        vPlanProvider.execute();
    }

}
