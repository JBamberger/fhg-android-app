package de.fhg_radolfzell.android_app.main;

import dagger.Subcomponent;
import de.fhg_radolfzell.android_app.main.calendar.CalendarComponent;
import de.fhg_radolfzell.android_app.main.calendar.CalendarModule;
import de.fhg_radolfzell.android_app.main.feed.FeedComponent;
import de.fhg_radolfzell.android_app.main.feed.FeedModule;
import de.fhg_radolfzell.android_app.main.settings.SettingsComponent;
import de.fhg_radolfzell.android_app.main.settings.SettingsModule;
import de.fhg_radolfzell.android_app.main.vplan.VPlanComponent;
import de.fhg_radolfzell.android_app.main.vplan.VPlanModule;

/**
 * @author Jannik
 * @version 06.08.2016.
 */
@MainScope
@Subcomponent(modules=MainModule.class)
public interface MainComponent {
    VPlanComponent newVPlanComponent(VPlanModule vPlanModule);

    CalendarComponent newCalendarComponent(CalendarModule calendarModule);

    FeedComponent newFeedComponent(FeedModule feedModule);

    SettingsComponent newSettingsComponent(SettingsModule settingsModule);

    void inject(MainActivity activity);
}
