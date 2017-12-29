package de.jbamberger.api.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class FeedItem {

    @SerializedName("id")
    public int id;
    @SerializedName("date")
    public String date;
    @SerializedName("link")
    public String link;
    @SerializedName("title")
    public Title title;
    @SerializedName("excerpt")
    public Excerpt excerpt;

    public static class Title {
        @SerializedName("rendered")
        public String rendered;

        @Override
        public String toString() {
            return "Title{" +
                    "rendered='" + rendered + '\'' +
                    '}';
        }
    }

    public static class Excerpt {
        @SerializedName("rendered")
        public String rendered;

        @Override
        public String toString() {
            return "Excerpt{" +
                    "rendered='" + rendered + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", link='" + link + '\'' +
                ", title=" + title +
                ", excerpt=" + excerpt +
                '}';
    }
}