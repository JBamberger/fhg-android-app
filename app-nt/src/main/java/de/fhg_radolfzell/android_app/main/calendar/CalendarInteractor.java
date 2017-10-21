package de.fhg_radolfzell.android_app.main.calendar;

public interface CalendarInteractor {
    void getCalendar();
    void getICALCalendar(); //Results are delivered through the shared Bus.
    void getXMLCalendar(); //Results are delivered through the shared Bus.
}
