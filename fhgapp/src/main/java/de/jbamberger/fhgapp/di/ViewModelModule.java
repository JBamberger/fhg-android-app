package de.jbamberger.fhgapp.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import de.jbamberger.fhgapp.ui.MainViewModel;
import de.jbamberger.fhgapp.ui.contact.ContactViewModel;
import de.jbamberger.fhgapp.ui.feed.FeedViewModel;
import de.jbamberger.fhgapp.ui.vplan.VPlanViewModel;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel.class)
    abstract ViewModel bindFeedViewModel(FeedViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(VPlanViewModel.class)
    abstract ViewModel bindVPlanViewModel(VPlanViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel.class)
    abstract ViewModel bindContactViewModel(ContactViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel viewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
