package de.jbamberger.api.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Entity(tableName = "feedItems")
class FeedItem {

    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    @SerializedName("date")
    var date: String? = null

    @ColumnInfo(name = "link")
    @SerializedName("link")
    var link: String? = null

    @Embedded
    @SerializedName("title")
    var title: Title? = null

    @Embedded
    @SerializedName("excerpt")
    var excerpt: Excerpt? = null


    class Title {
        @ColumnInfo(name = "renderedTitle")
        @SerializedName("rendered")
        var rendered: String? = null

        override fun toString(): String {
            return "Title{" +
                    "rendered='" + rendered + '\'' +
                    '}'
        }
    }


    class Excerpt {
        @ColumnInfo(name = "renderedExcerpt")
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