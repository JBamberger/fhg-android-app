package de.jbamberger.fhgapp.ui.vplan;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.jbamberger.api.data.VPlan;
import de.jbamberger.api.data.VPlanDay;
import de.jbamberger.api.data.VPlanRow;
import de.jbamberger.fhgapp.R;
import de.jbamberger.fhgapp.RefreshableListFragmentBinding;
import de.jbamberger.fhgapp.source.Resource;
import de.jbamberger.fhgapp.source.Status;
import de.jbamberger.fhgapp.ui.components.BaseFragment;
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
public class VPlanFragment extends BaseFragment<VPlanViewModel>
        implements SwipeRefreshLayout.OnRefreshListener,
        Observer<Resource<VPlan>> {

    private RefreshableListFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.refreshable_list_fragment, container, false);
        binding.container.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.setListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.init();
        binding.setIsRefreshing(true);
        viewModel.getVPlan().observe(this, this);
    }

    @Override
    public void onRefresh() {
        binding.setIsRefreshing(true);
        viewModel.getVPlan().removeObserver(this);
        viewModel.refresh();
        viewModel.getVPlan().observe(this, this);
    }

    @Override
    public void onChanged(@Nullable Resource<VPlan> vPlanResource) {
        if (vPlanResource == null) return;
        if (vPlanResource.status == Status.SUCCESS && vPlanResource.data != null) {
            binding.container.setAdapter(new VPlanAdapter(vPlanResource.data));
            binding.setIsRefreshing(false);
        }
    }

    @Override
    public Class<VPlanViewModel> getViewModelClass() {
        return VPlanViewModel.class;
    }


    private static final class VPlanAdapter extends DataBindingBaseAdapter {

        private final List<VPlanRow> rows1;
        private final List<VPlanRow> rows2;
        private final VPlanHeader header1;
        private final VPlanHeader header2;
        private final int bound1;
        private final int bound2;


        VPlanAdapter(@NonNull VPlan vPlan) {
            VPlanDay day1 = vPlan.getDay1();
            VPlanDay day2 = vPlan.getDay2();

            rows1 = day1.getVPlanRows();
            rows2 = day2.getVPlanRows();
            bound1 = rows1.size() + 1;
            bound2 = bound1 + rows2.size() + 1;
            header1 = new VPlanHeader(day1.getDateAndDay(), day1.getLastUpdated(), day1.getMotd());
            header2 = new VPlanHeader(day2.getDateAndDay(), day2.getLastUpdated(), day2.getMotd());
        }

        @Override
        protected Object getObjForPosition(int position) {
            if (position == 0) {
                return header1;
            } else if (0 < position && position < bound1) {
                return rows1.get(position - 1);
            } else if (position == bound1) {
                return header2;
            } else if (bound1 < position && position < bound2) {
                return rows2.get(position - bound1 - 1);
            } else if (position == bound2) {
                return null; // footer
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        protected Object getListenerForPosition(int position) {
            return null;
        }

        @Override
        protected int getLayoutIdForPosition(int position) {
            if (position == 0 || position == bound1) {
                return R.layout.vplan_header;
            } else if ((0 < position && position < bound1)
                    || (bound1 < position && position < bound2)) {
                return R.layout.vplan_item;
            } else if (position == bound2) {
                return R.layout.vplan_footer;
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public int getItemCount() {
            return bound2;
        }
    }
}
