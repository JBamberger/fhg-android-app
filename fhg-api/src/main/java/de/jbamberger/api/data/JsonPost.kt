package de.jbamberger.api.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class JsonPost {

    @SerializedName("id")
    @Expose
    var id: Int = 0
    @SerializedName("date_gmt")
    @Expose
    var dateGmt: String? = null
    @SerializedName("modified_gmt")
    @Expose
    var modifiedGmt: String? = null
    @SerializedName("slug")
    @Expose
    var slug: String? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("title")
    @Expose
    var title: Title? = null
    @SerializedName("content")
    @Expose
    var content: Content? = null
    @SerializedName("excerpt")
    @Expose
    var excerpt: Excerpt? = null
    @SerializedName("featured_media")
    @Expose
    var featuredMedia: Int = 0
    @SerializedName("meta")
    @Expose
    var meta: List<Any>? = null
    @SerializedName("_links")
    @Expose
    var links: Links? = null

    inner class Guid {

        @SerializedName("rendered")
        @Expose
        var rendered: String? = null

    }

    inner class Title {

        @SerializedName("rendered")
        @Expose
        var rendered: String? = null

    }

    inner class Content {

        @SerializedName("rendered")
        @Expose
        var rendered: String? = null

    }

    inner class Excerpt {

        @SerializedName("rendered")
        @Expose
        var rendered: String? = null

    }

    inner class Links {

        @SerializedName("self")
        @Expose
        var self: List<Self>? = null
        @SerializedName("wp:featuredmedia")
        @Expose
        var wpFeaturedmedia: List<WpFeaturedmedium>? = null
        @SerializedName("wp:attachment")
        @Expose
        var wpAttachment: List<WpAttachment>? = null


        inner class Self {

            @SerializedName("href")
            @Expose
            var href: String? = null

        }

        inner class WpFeaturedmedium {

            @SerializedName("embeddable")
            @Expose
            var embeddable: Boolean? = null
            @SerializedName("href")
            @Expose
            var href: String? = null

        }

        inner class WpAttachment {

            @SerializedName("href")
            @Expose
            var href: String? = null

        }
    }
}