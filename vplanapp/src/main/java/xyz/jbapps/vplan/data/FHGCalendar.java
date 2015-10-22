package xyz.jbapps.vplan.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import xyz.jbapps.vplan.util.FHGCalendarParser;
import xyz.jbapps.vplan.util.FHGCalendarProvider;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;

/**
 * Represents the current calendar content.
 */
public class FHGCalendar {
    @SerializedName("http_last_updated")
    public long lastUpdated = 0;
    @SerializedName("fhg_feed")
    public List<FHGCalendarParser.FHGCalendarEntry> calendarEntrys = new ArrayList<>();
}
