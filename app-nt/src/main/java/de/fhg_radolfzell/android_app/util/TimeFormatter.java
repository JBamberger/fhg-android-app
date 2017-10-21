package de.fhg_radolfzell.android_app.util;

import android.content.Context;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.data.CalendarEvent;

/**
 * @author Jannik
 * @version 14.08.2016.
 */
public class TimeFormatter {

    private static final String PATTERN_CALENDAR_FULL_DAY = "....-..-.. 00:00:00";
    private static final String PATTERN_CALENDAR_TIME = "HH:mm";
    private static final String PATTERN_CALENDAR_DATE = "dd.MM.yyyy";
    private static final String PATTERN_CALENDAR_DATE_TIME = "dd.MM.yyyy HH:mm";
    private static final String PATTERN_EVENT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";
    private Context context;

    public TimeFormatter(Context context) {
        this.context = context;
    }

    public static boolean isCalendarEventAllDay(CalendarEvent calendarEvent) {
        if (calendarEvent.getStartDate() != null) {
            if (calendarEvent.getEndDate() != null) {
                return calendarEvent.getEndDate().matches(PATTERN_CALENDAR_FULL_DAY) && calendarEvent.getStartDate().matches(PATTERN_CALENDAR_FULL_DAY);
            }
            return calendarEvent.getStartDate().matches(PATTERN_CALENDAR_FULL_DAY);
        }
        return false;
    }

    public static String formatFeedTimeStamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormat.forPattern(PATTERN_EVENT_TIMESTAMP)).toString(PATTERN_CALENDAR_DATE_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    public String formatCalendarEvent(CalendarEvent calendarEvent) throws IllegalArgumentException {
        if(calendarEvent == null) return "";
        if (calendarEvent.getStartDate() != null) {
            if (calendarEvent.getEndDate() != null) {
//                all non empty
                if (calendarEvent.getEndDate().matches(PATTERN_CALENDAR_FULL_DAY) && calendarEvent.getStartDate().matches(PATTERN_CALENDAR_FULL_DAY)) {
                    return context.getResources().getString(R.string.calendar_event_day_to_day,
                            calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_DATE),
                            calendarEvent.getEndDateObject().toString(PATTERN_CALENDAR_DATE));
                } else {
                    if (Days.daysBetween(calendarEvent.getStartDateObject(), calendarEvent.getEndDateObject()).getDays() == 0) {
                        return context.getResources().getString(R.string.calendar_event_in_day,
                                calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_DATE),
                                calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_TIME),
                                calendarEvent.getEndDateObject().toString(PATTERN_CALENDAR_TIME));
                    }
                    return context.getResources().getString(R.string.calendar_event_day_to_day,
                            calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_DATE_TIME),
                            calendarEvent.getEndDateObject().toString(PATTERN_CALENDAR_DATE_TIME));
                }
            } else {
                //                enddate empty
                if (calendarEvent.getStartDate().matches(PATTERN_CALENDAR_FULL_DAY)) {
                    return context.getResources().getString(R.string.calendar_event_day, calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_DATE));
                } else {
                    return context.getResources().getString(R.string.calendar_event_day, calendarEvent.getStartDateObject().toString(PATTERN_CALENDAR_DATE_TIME));
                }
            }

        } else {
            throw new IllegalArgumentException("calendarEvent has empty start dateAt");
        }
    }
}
