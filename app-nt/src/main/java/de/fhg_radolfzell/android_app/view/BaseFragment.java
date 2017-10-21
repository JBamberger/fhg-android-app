package de.fhg_radolfzell.android_app.view;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.FHGApplication;
import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.databinding.FragmentBaseBinding;
import timber.log.Timber;

/**
 * @author Jannik Bamberger
 * @version @version 05.08.2016.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    @Inject
    protected Context context;

    @Inject
    protected Bus eventBus;

    protected FragmentBaseBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FHGApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        eventBus.register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            eventBus.unregister(this);
        } catch (Exception e) {
            Timber.e(e, "onPause: bus unregistration failed");
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);
        binding.setListener(this);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.brand_primary, R.color.brand_primary, R.color.brand_primary, R.color.brand_primary);//TODO: color
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.hasFixedSize();

        return binding.getRoot();
    }

    public abstract void update();

    protected void showLoadingIndicator(boolean active) {
        if (active) {
            binding.swipeRefreshLayout.setRefreshing(true);
            binding.floatingActionButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_rotate_animation));
        } else {
            binding.floatingActionButton.clearAnimation();
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

}
