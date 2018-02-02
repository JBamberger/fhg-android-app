package de.jbamberger.fhgapp.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.ui.about.AboutActivity
import de.jbamberger.fhgapp.ui.contact.ContactFragment
import de.jbamberger.fhgapp.ui.feed.FeedFragment
import de.jbamberger.fhgapp.ui.settings.SettingsActivity
import de.jbamberger.fhgapp.ui.vplan.VPlanFragment

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */


@Module
internal abstract class BuildersModule {

    //TODO: scope
    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeAboutActivity(): AboutActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector
    internal abstract fun contributesVPlanFragment(): VPlanFragment

    @ContributesAndroidInjector
    internal abstract fun contributesFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    internal abstract fun contributesContactFragment(): ContactFragment

}
