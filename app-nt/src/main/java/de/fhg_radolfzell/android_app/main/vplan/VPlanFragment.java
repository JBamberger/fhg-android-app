package de.fhg_radolfzell.android_app.main.vplan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.main.MainActivity;
import de.fhg_radolfzell.android_app.main.MainContract;
import de.fhg_radolfzell.android_app.view.BaseFragment;

import static de.fhg_radolfzell.android_app.util.Preconditions.checkNotNull;

/**
 * @author Jannik
 * @version 29.07.2016.
 */
public class VPlanFragment extends BaseFragment implements VPlanContract.View {

    private static final String TAG = "VPlanFragment";

    @Inject
    VPlanAdapter adapter;

    @Inject
    VPlanContract.Presenter mPresenter;

    VPlanComponent vPlanComponent;

    MainContract.View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vPlanComponent = ((MainActivity) getActivity()).getMainComponent().newVPlanComponent(new VPlanModule(this));
        vPlanComponent.inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        vPlanComponent = null;
        super.onDestroy();
    }

    public void update() {
        mPresenter.loadVPlan();
    }

    public void setMainView(@NonNull MainContract.View view) {
        mMainView = checkNotNull(view, "MainView can not be null");
    }

    @Override
    public void setPresenter(@NonNull VPlanContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "Presenter can not be null");
    }


    @Override
    public void setLoadingIndicator(boolean active) {
        showLoadingIndicator(active);
    }

    @Override
    public void showVPlan(VPlan[] vplan) {
        adapter.setData(vplan);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingVPlanError() {
        Snackbar.make(getView(), R.string.vplan_loading_failed, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setSubtitle(String subtitle) {
        mMainView.setSubtitle(subtitle);
    }

    @Override
    public void setSubtitle(@StringRes int subtitle) {
        mMainView.setSubtitle(subtitle);
    }

    @Override
    public void clearSubtitle() {
        mMainView.clearSubtitle();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
