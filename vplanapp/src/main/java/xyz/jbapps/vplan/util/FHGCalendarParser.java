package xyz.jbapps.vplan.util;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by Jannik on 17.10.2015.
 */
public class FHGCalendarParser {


    public class FHGCalendarEntry {
        @SerializedName("calendar_entry_title")
        public String title;
        @SerializedName("calendar_entry_summary")
        public String summary;
        @SerializedName("calendar_entry_date_start")
        public String updated_at;
        @SerializedName("calendar_entry_date_end")
        public String published_at;

        public void escape() {
            summary = StringEscapeUtils.escapeHtml4(summary);
            title = StringEscapeUtils.escapeHtml4(title);
        }

        public void unescape() {
            summary = StringEscapeUtils.unescapeHtml4(summary);
            title = StringEscapeUtils.unescapeHtml4(title);
        }

        public FHGCalendarEntry(String title, String author, String summary, String link, String updated_at, String published_at) {
            this.title = title;
            this.summary = summary;
            this.updated_at = updated_at;
            this.published_at = published_at;
        }
    }
}
