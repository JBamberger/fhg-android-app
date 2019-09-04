package de.jbamberger.fhgapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.jbamberger.fhgapp.ui.MainViewModel
import de.jbamberger.fhgapp.ui.about.AboutViewModel
import de.jbamberger.fhgapp.ui.contact.ContactViewModel
import de.jbamberger.fhgapp.ui.feed.FeedViewModel
import de.jbamberger.fhgapp.ui.vplan.VPlanViewModel

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    internal abstract fun bindFeedViewModel(viewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VPlanViewModel::class)
    internal abstract fun bindVPlanViewModel(viewModel: VPlanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel::class)
    internal abstract fun bindContactViewModel(viewModel: ContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    internal abstract fun bindAboutViewModel(viewModel: AboutViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
