package de.jbamberger.fhg.repository.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.squareup.moshi.Json

class FeedChunk(val items: List<FeedItem>)

@Entity(tableName = "feedItems")
class FeedItem {

    @PrimaryKey
    @Json(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    @Json(name = "date")
    var date: String? = null

    @ColumnInfo(name = "link")
    @Json(name = "link")
    var link: String? = null

    @Embedded
    @Json(name = "title")
    var title: Title? = null

    @Embedded
    @Json(name = "excerpt")
    var excerpt: Excerpt? = null


    class Title {
        @ColumnInfo(name = "renderedTitle")
        @Json(name = "rendered")
        var rendered: String? = null

        override fun toString(): String {
            return "Title{" +
                    "rendered='" + rendered + '\'' +
                    '}'
        }
    }


    class Excerpt {
        @ColumnInfo(name = "renderedExcerpt")
        @Json(name = "rendered")
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
