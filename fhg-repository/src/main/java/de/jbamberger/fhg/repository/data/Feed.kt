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

    @ColumnInfo(name = "slug")
    @Json(name = "slug")
    var slug: String? = null

    @ColumnInfo(name = "type")
    @Json(name = "type")
    var type: String? = null

    @ColumnInfo(name = "link")
    @Json(name = "link")
    var link: String? = null

    @Embedded
    @Json(name = "title")
    var title: Title? = null

    @Embedded
    @Json(name = "excerpt")
    var excerpt: Excerpt? = null

    @ColumnInfo(name = "author")
    @Json(name = "author")
    var author: Int? = null

    @ColumnInfo(name = "featuredMedia")
    @Json(name = "featured_media")
    var featuredMedia: Int? = null

    @JsonClass(generateAdapter = true)
    class Title {
        @ColumnInfo(name = "renderedTitle")
        @Json(name = "rendered")
        var rendered: String? = null

        override fun toString() = "Title{rendered='$rendered'}"
    }


    @JsonClass(generateAdapter = true)
    class Excerpt {
        @ColumnInfo(name = "renderedExcerpt")
        @Json(name = "rendered")
        var rendered: String? = null

        override fun toString() = "Excerpt{rendered='$rendered'}"
    }

    override fun toString() =
        "FeedItem{id=$id, date='$date', link='$link', title=$title, excerpt=$excerpt}"
}

@JsonClass(generateAdapter = true)
class FeedMedia(
    val id: Int,
    val date: String,
    val slug: String,
    val type: String,
    val link: String,
    val title: Title,
    val author: Int,
    val caption: Caption,
    val alt_text: String,
    val media_type: String,
    val mime_type: String,
    val media_details: MediaDetails,
    val source_url: String
) {

    @JsonClass(generateAdapter = true)
    data class Title(val rendered: String)

    @JsonClass(generateAdapter = true)
    data class Caption(val rendered: String)

    @JsonClass(generateAdapter = true)
    data class MediaDetails(
        val width: Int,
        val height: Int,
        val file: String,
        val sizes: Map<String, ImageSize>
    )

    @JsonClass(generateAdapter = true)
    data class ImageSize(
        val file: String,
        val width: Int,
        val height: Int,
        val mime_type: String,
        val source_url: String
    )
}
