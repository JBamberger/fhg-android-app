package de.jbamberger.fhgapp.ui.vplan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.jbamberger.fhgapp.R;
import de.jbamberger.fhgapp.VPlanFragmentBinding;
import de.jbamberger.fhgapp.ui.components.BaseFragment;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanFragment extends BaseFragment<VPlanViewModel> {

    private VPlanFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.vplan_fragment, container, false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Class<VPlanViewModel> getViewModelClass() {
        return VPlanViewModel.class;
    }
}
