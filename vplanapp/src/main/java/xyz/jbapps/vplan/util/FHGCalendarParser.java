package xyz.jbapps.vplan.util;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Used to parse calendar entries
 *
 * TODO: finish implementation
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
public class FHGCalendarParser {


    public class FHGCalendarEntry {
        @SerializedName("calendar_entry_title")
        public String title;
        @SerializedName("calendar_entry_summary")
        public String summary;
        @SerializedName("calendar_entry_full_day")
        public boolean fullDay;
        @SerializedName("calendar_entry_date_start")
        public String startDate;
        @SerializedName("calendar_entry_date_end")
        public String endDate;
        @SerializedName("calendar_entry_permanent_link")
        public String permanentLink;

        public void escape() {
            summary = StringEscapeUtils.escapeHtml4(summary);
            title = StringEscapeUtils.escapeHtml4(title);
        }

        public void unescape() {
            summary = StringEscapeUtils.unescapeHtml4(summary);
            title = StringEscapeUtils.unescapeHtml4(title);
        }

        public FHGCalendarEntry(String title, String summary, boolean fullDay, String startDate, String endDate, String permanentLink) {
            this.title = title;
            this.summary = summary;
            this.fullDay = fullDay;
            this.startDate = startDate;
            this.endDate = endDate;
            this.permanentLink = permanentLink;
        }
    }
}
