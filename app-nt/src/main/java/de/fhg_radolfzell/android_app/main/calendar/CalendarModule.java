package de.fhg_radolfzell.android_app.main.calendar;

import android.app.Activity;

import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
public class CalendarModule {

    private CalendarFragment calendarFragment;

    public CalendarModule(CalendarFragment calendarFragment) {
        this.calendarFragment = calendarFragment;
    }

    @Provides
    @CalendarScope
    public CalendarFragment providesCalendarFragment() {
        return calendarFragment;
    }

    @Provides
    @CalendarScope
    public CalendarInteractor providesCalendarInteractor(Bus bus, FhgWebInterface web, FhgApiInterface api) {
        return new CalendarInteractorImpl(bus, web, api);
    }

    @Provides
    @CalendarScope
    public CalendarAdapter providesCalendarAdapter(Activity a) {
        return new CalendarAdapter(a);
    }
}
