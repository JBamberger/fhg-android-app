package de.jbamberger.fhgapp.ui.vplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.jbamberger.fhg_parser.VPlanRow;
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

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.init();

        viewModel.getVPlan().observe(this, vPlanSetResource -> {
            Timber.d(vPlanSetResource.toString());
            if (vPlanSetResource.status == Status.SUCCESS) {
                for (VPlanRow vPlanRow : vPlanSetResource.data.getDay1().getPlan().getVPlanRows()) {
                    Timber.d(vPlanRow.toString());
                }
            }
        });
    }

    @Override
    public Class<VPlanViewModel> getViewModelClass() {
        return VPlanViewModel.class;
    }
}
