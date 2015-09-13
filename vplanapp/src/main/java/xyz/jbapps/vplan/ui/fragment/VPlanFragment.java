package xyz.jbapps.vplan.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.ui.MultiVPlanAdapter;
import xyz.jbapps.vplan.util.GradeSorter;
import xyz.jbapps.vplan.util.Property;
import xyz.jbapps.vplan.util.VPlanProvider;

public class VPlanFragment extends LoadingRecyclerViewFragment {

    private static final String TAG = "VPlanFragment";
    private static final String STATE_SHOULD_REFRESH = "refresh";

    private final VPlanListener mVPlanListener = new VPlanListener();

    private Property mProperty;
    private VPlanProvider vPlanProvider;
    private MultiVPlanAdapter multiVPlanAdapter;

    private String gradeState;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater,container,savedInstanceState);

        setActionBarTitle(R.string.title_fragment_vplan);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
            }
        });
        multiVPlanAdapter = new MultiVPlanAdapter(context);
        recyclerView.setAdapter(multiVPlanAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vplan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_force_reload) {
            Log.d(TAG, "invoked force reload");
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_FORCE_LOAD);
            return true;
        }
        if (id == R.id.action_set_grade) {
            showGradePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        mProperty = new Property(context);
        gradeState = mProperty.readGrade();
        stateShouldRefresh = savedInstanceState == null || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true);
    }

    private boolean stateShouldRefresh = true;

    @Override
    public void onStart() {
        super.onStart();
        updateActionBar();
        if (mProperty.getShowGradePicker()) {
            showGradePicker();
        }
        if (stateShouldRefresh) {
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
        } else {
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_CACHE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!gradeState.equals(mProperty.readGrade())) {
            //returning from settings: update title and list
            gradeState = mProperty.readGrade();
            mVPlanListener.loadVPlan(VPlanProvider.TYPE_CACHE);
        }
    }

    private void updateActionBar() {
        String subtitle = gradeState;
        if (subtitle != null && subtitle.equals("")) {
            subtitle = "Alles";
        }
        setActionBarSubtitle(subtitle);
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
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                selected.add(i);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);

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
                        Collections.sort(selected);
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
                        updateActionBar();
                        mVPlanListener.loadVPlan(VPlanProvider.TYPE_LOAD);
                    }
                })
                .setNegativeButton(R.string.select_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mProperty.storeGrade("");
                        mProperty.setShowGradePicker(false);
                        gradeState = "";
                        updateActionBar();
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

    private class VPlanListener implements VPlanProvider.IVPlanResultListener {

        @Override
        public void vPlanLoadingFailed() {
            toggleLoading(false);
            Toast.makeText(context, "Loading failed", Toast.LENGTH_LONG).show(); // TODO: use resources
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
            updateNetworkFlags();
            if(!wifiConnected && !mobileConnected) {
                toggleLoading(false);
                Toast.makeText(context, R.string.text_network_disconnected, Toast.LENGTH_LONG).show();
                return;
            }
            if(method == VPlanProvider.TYPE_LOAD && wifiConnected) {
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
}
