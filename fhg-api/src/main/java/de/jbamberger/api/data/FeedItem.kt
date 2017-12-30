package de.jbamberger.api.data

import com.google.gson.annotations.SerializedName

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FeedItem {

    @SerializedName("id")
    var id: Int = 0
    @SerializedName("date")
    var date: String? = null
    @SerializedName("link")
    var link: String? = null
    @SerializedName("title")
    var title: Title? = null
    @SerializedName("excerpt")
    var excerpt: Excerpt? = null

    class Title {
        @SerializedName("rendered")
        var rendered: String? = null

        override fun toString(): String {
            return "Title{" +
                    "rendered='" + rendered + '\'' +
                    '}'
        }
    }

    class Excerpt {
        @SerializedName("rendered")
        var rendered: String? = null

        override fun toString(): String {
            return "Excerpt{" +
                    "rendered='" + rendered + '\'' +
                    '}'
        }
    }

    override fun toString(): String {
        return "FeedItem{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", link='" + link + '\'' +
                ", title=" + title +
                ", excerpt=" + excerpt +
                '}'
    }
}