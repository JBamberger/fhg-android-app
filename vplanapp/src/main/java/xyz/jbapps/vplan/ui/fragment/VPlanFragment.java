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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.ui.MultiVPlanAdapter;
import xyz.jbapps.vplan.util.Property;
import xyz.jbapps.vplan.util.VPlanProvider;
import xyz.jbapps.vplan.util.VPlanSorter;
import xyz.jbapps.vplan.util.network.VPlanRequest;

public class VPlanFragment extends LoadingRecyclerViewFragment {

    private static final String TAG = "VPlanFragment";

    private static final String TAG_VPLAN1 = "VPlan1";
    private static final String TAG_VPLAN2 = "VPlan2";
    private static final String URL_VPLAN1 = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String URL_VPLAN2 = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    private boolean vPlan1Loaded = false;
    private boolean vPlan2Loaded = false;

    private RequestQueue netQueue;
    private VPlanData vPlanCache;
    private Property mProperty;
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
            loadVPlan();
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
                loadVPlan();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVPlan();
            }
        });
        multiVPlanAdapter = new MultiVPlanAdapter(context);
        recyclerView.setAdapter(multiVPlanAdapter);

            loadVPlan();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        netQueue = Volley.newRequestQueue(context);
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
            loadVPlan();
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

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            toggleLoading(false);
            Toast.makeText(context, getString(R.string.text_loading_failed), Toast.LENGTH_LONG).show();
        }
    };

    private void applyData(VPlanData vplan1, VPlanData vplan2) {
        toggleLoading(false);
        VPlanSorter sorter = new VPlanSorter(gradeState, courseState);
        vplan1 = sorter.filterData(vplan1);
        vplan2 = sorter.filterData(vplan2);
        multiVPlanAdapter.setData(vplan1, vplan2);
    }

    private void loadVPlan() {
        toggleLoading(true);
        netQueue.cancelAll(TAG_VPLAN1);
        netQueue.cancelAll(TAG_VPLAN2);

        VPlanRequest vPlanRequest1 = new VPlanRequest(URL_VPLAN1, new Response.Listener<VPlanData>() {
            @Override
            public void onResponse(VPlanData data) {
                if (vPlan2Loaded) {
                    vPlan1Loaded = false;
                    vPlan2Loaded = false;
                    applyData(data, vPlanCache);
                } else {
                    vPlan1Loaded = true;
                    vPlanCache = data;
                }
            }
        }, errorListener);
        vPlanRequest1.setTag(TAG_VPLAN1);
        VPlanRequest vPlanRequest2 = new VPlanRequest(URL_VPLAN2, new Response.Listener<VPlanData>() {
            @Override
            public void onResponse(VPlanData data) {
                if (vPlan1Loaded) {
                    vPlan1Loaded = false;
                    vPlan2Loaded = false;
                    applyData(vPlanCache, data);
                } else {
                    vPlan2Loaded = true;
                    vPlanCache = data;
                }
            }
        }, errorListener);
        vPlanRequest2.setTag(TAG_VPLAN2);

        netQueue.add(vPlanRequest1);
        netQueue.add(vPlanRequest2);
    }

}
