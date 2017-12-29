package de.jbamberger.fhgapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.jbamberger.fhgapp.ui.MainActivity;
import de.jbamberger.fhgapp.ui.feed.FeedFragment;
import de.jbamberger.fhgapp.ui.vplan.VPlanFragment;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */


@Module
abstract class BuildersModule {

    //TODO: scope
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract VPlanFragment contributesVPlanFragment();

    @ContributesAndroidInjector
    abstract FeedFragment contributesFeedFragment();

}
