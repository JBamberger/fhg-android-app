package de.jbamberger.fhgapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.jbamberger.fhgapp.ui.MainActivity;
import de.jbamberger.fhgapp.ui.about.AboutActivity;
import de.jbamberger.fhgapp.ui.contact.ContactFragment;
import de.jbamberger.fhgapp.ui.feed.FeedFragment;
import de.jbamberger.fhgapp.ui.settings.SettingsActivity;
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
    abstract AboutActivity contributeAboutActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector
    abstract VPlanFragment contributesVPlanFragment();

    @ContributesAndroidInjector
    abstract FeedFragment contributesFeedFragment();

    @ContributesAndroidInjector
    abstract ContactFragment contributesContactFragment();

}
