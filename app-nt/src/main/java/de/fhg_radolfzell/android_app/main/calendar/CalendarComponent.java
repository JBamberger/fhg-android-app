package de.fhg_radolfzell.android_app.main.calendar;

import dagger.Subcomponent;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@CalendarScope
@Subcomponent(
        modules = {
                CalendarModule.class
        }
)
public interface CalendarComponent {

    void inject(CalendarFragment fragment);

    void inject(CalendarInteractorImpl interactor);
}
