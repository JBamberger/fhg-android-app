package de.jbamberger.api.data

import com.squareup.moshi.Json

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class JsonPost {

    @Json(name = "id")
    var id: Int = 0
    @Json(name = "date_gmt")
    var dateGmt: String? = null
    @Json(name = "modified_gmt")
    var modifiedGmt: String? = null
    @Json(name = "slug")
    var slug: String? = null
    @Json(name = "link")
    var link: String? = null
    @Json(name = "title")
    var title: Title? = null
    @Json(name = "content")
    var content: Content? = null
    @Json(name = "excerpt")
    var excerpt: Excerpt? = null
    @Json(name = "featured_media")
    var featuredMedia: Int = 0
    @Json(name = "meta")
    var meta: List<Any>? = null
    @Json(name = "_links")
    var links: Links? = null

    inner class Guid {

        @Json(name = "rendered")
        var rendered: String? = null

    }

    inner class Title {

        @Json(name = "rendered")
        var rendered: String? = null

    }

    inner class Content {

        @Json(name = "rendered")
        var rendered: String? = null

    }

    inner class Excerpt {

        @Json(name = "rendered")
        var rendered: String? = null

    }

    inner class Links {

        @Json(name = "self")
        var self: List<Self>? = null
        @Json(name = "wp:featuredmedia")
        var wpFeaturedmedia: List<WpFeaturedmedium>? = null
        @Json(name = "wp:attachment")
        var wpAttachment: List<WpAttachment>? = null


        inner class Self {

            @Json(name = "href")
            var href: String? = null

        }

        inner class WpFeaturedmedium {

            @Json(name = "embeddable")
            var embeddable: Boolean? = null
            @Json(name = "href")
            var href: String? = null

        }

        inner class WpAttachment {

            @Json(name = "href")
            var href: String? = null

        }
    }
}