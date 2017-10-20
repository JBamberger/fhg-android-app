package de.jbamberger.fhgapp.ui.components;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import de.jbamberger.fhgapp.di.Injectable;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public abstract class BaseFragment<T extends ViewModel> extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private T viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModelClass());
    }

    public abstract Class<T> getViewModelClass();
}