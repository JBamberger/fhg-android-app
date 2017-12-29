package de.jbamberger.fhgapp.ui.vplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.jbamberger.fhgapp.R;
import de.jbamberger.fhgapp.VPlanFragmentBinding;
import de.jbamberger.fhgapp.source.Status;
import de.jbamberger.fhgapp.ui.components.BaseFragment;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanFragment extends BaseFragment<VPlanViewModel> {

    private VPlanFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.vplan_fragment, container, false);
        binding.vplanContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.init();

        viewModel.getVPlan().observe(this, vPlanResource -> {
            if(vPlanResource == null) return;
            Timber.d("%s, %s, %s", vPlanResource.message, vPlanResource.status, vPlanResource.data);
            if (vPlanResource.status == Status.SUCCESS && vPlanResource.data != null) {
                VPlanAdapter adapter = new VPlanAdapter(vPlanResource.data);
                binding.vplanContainer.setAdapter(adapter);
            }
        });
    }

    @Override
    public Class<VPlanViewModel> getViewModelClass() {
        return VPlanViewModel.class;
    }
}
