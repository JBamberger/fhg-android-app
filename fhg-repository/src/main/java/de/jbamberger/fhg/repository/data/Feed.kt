package de.jbamberger.fhg.repository.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "feedItems")
@JsonClass(generateAdapter = true)
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

    @ColumnInfo(name = "featuredMedia")
    @Json(name = "featured_media")
    var featuredMedia: Int? = null

    @Embedded
    @Json(name = "title")
    var title: Title? = null

    @Embedded
    @Json(name = "excerpt")
    var excerpt: Excerpt? = null


    @JsonClass(generateAdapter = true)
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


    @JsonClass(generateAdapter = true)
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

@Entity(tableName = "feedMedia")
@JsonClass(generateAdapter = true)
class FeedMedia {

    @PrimaryKey
    @Json(name = "id")
    var id: Int = 0

    @Json(name = "date")
    var date: String? = null

    @Json(name = "media_type")
    var mediaType: String? = null

    @Json(name = "mime_type")
    var mimeType: String? = null

    @Json(name = "media_details")
    @Embedded(prefix = "detail_")
    var mediaDetails: MediaDetails? = null

    @Json(name = "caption")
    var caption: Caption? = null

    @Json(name = "source_url")
    var sourceUrl: String? = null

    @JsonClass(generateAdapter = true)
    class Caption {
        @Json(name = "rendered")
        var rendered: String? = null
    }

    @JsonClass(generateAdapter = true)
    class MediaDetails {
        @Json(name = "width")
        var width: Int? = null

        @Json(name = "height")
        var height: Int? = null

        @Json(name = "file")
        var file: String? = null

        @Json(name = "sizes")
        @Embedded(prefix = "sizes_")
        var sizes: Sizes? = null

        @JsonClass(generateAdapter = true)
        class Sizes {
            @Json(name = "thumbnail")
            @Embedded
            var thumbnail: Size? = null

            @Json(name = "medium")
            @Embedded
            var medium: Size? = null

            @Json(name = "medium_large")
            @Embedded
            var mediumLarge: Size? = null

            @Json(name = "large")
            @Embedded
            var large: Size? = null

            @Json(name = "full")
            @Embedded
            var full: Size? = null


            @JsonClass(generateAdapter = true)
            class Size {
                @Json(name = "file")
                var file: String? = null

                @Json(name = "width")
                var width: Int? = null

                @Json(name = "height")
                var height: Int? = null

                @Json(name = "mime_type")
                var mimeType: String? = null

                @Json(name = "source_url")
                var sourceUrl: String? = null
            }
        }
    }
}
