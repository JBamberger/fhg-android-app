package de.fhg_radolfzell.android_app.event;

import de.fhg_radolfzell.android_app.data.CalendarEvent;

public class CalendarLoadedEvent {

    public CalendarEvent[] events;

    public CalendarLoadedEvent(CalendarEvent[] events) {
        this.events = events;
    }
}
