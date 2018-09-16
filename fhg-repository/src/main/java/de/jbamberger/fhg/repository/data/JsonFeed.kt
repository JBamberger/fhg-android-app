package de.jbamberger.fhg.repository.data

import android.net.Uri
import com.squareup.moshi.Json
import org.joda.time.DateTime

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

data class JsonFeed(val items: List<JsonFeedItem>)

data class JsonFeedItem(
        @Json(name = "date") val date: DateTime,
        @Json(name = "date_gmt") val dateGmt: DateTime,
        @Json(name = "guid") val guid: JsonGuid,
        @Json(name = "id") val id: Int,
        @Json(name = "link") val link: Uri,
        @Json(name = "modified") val modified: DateTime,
        @Json(name = "modified_gmt") val modifiedGmt: DateTime,
        @Json(name = "slug") val slug: String,
        @Json(name = "status") val status: JsonStatus,
        @Json(name = "type") val type: String,
        @Json(name = "title") val title: JsonTitle,
        @Json(name = "content") val content: JsonContent,
        @Json(name = "author") val author: Int,
        @Json(name = "excerpt") val excerpt: JsonExcerpt,
        @Json(name = "featured_media") val featuredMedia: Int,
        @Json(name = "comment_status") val commentStatus: JsonCommentStatus,
        @Json(name = "ping_status") val pingStatus: JsonPingStatus,
        @Json(name = "format") val format: JsonFormat,
        @Json(name = "meta") val meta: JsonMeta,
        @Json(name = "sticky") val sticky: Boolean,
        @Json(name = "template") val template: String,
        @Json(name = "categories") val categories: List<JsonCategory>,
        @Json(name = "tags") val tags: List<JsonTag>)

enum class JsonStatus { PUBLISHED, FUTURE, DRAFT, PENDING, PRIVATE }
enum class JsonCommentStatus { OPEN, CLOSED }
enum class JsonPingStatus { OPEN, CLOSED }
enum class JsonFormat { STANDARD, ASIDE, CHAT, GALLERY, LINK, IMAGE, QUOTE, STATUS, VIDEO, AUDIO }

data class JsonGuid(
        val rendered: String
)

class JsonTitle(
        val rendered: String
)
class JsonContent(
        val rendered: String,
        val protected: Boolean
)
class JsonExcerpt(
        val rendered: String,
        val protected: Boolean
)
class JsonMeta
class JsonCategory
class JsonTag