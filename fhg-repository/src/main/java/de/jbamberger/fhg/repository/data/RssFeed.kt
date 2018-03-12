package de.jbamberger.api.data

import org.simpleframework.xml.*

@Root
internal class RssFeed {

    @Attribute
    var version: String? = null

    @Element
    var channel: Channel? = null

    override fun toString(): String {
        return "RssFeed{" +
                "version='" + version + '\'' +
                ", channel=" + channel +
                '}'
    }

    @NamespaceList(Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom"))
    @Root(strict = false)
    internal class Channel {
        // Tricky part in Simple XML because the link is named twice
        @ElementList(entry = "link", inline = true, required = false, type = Link::class)
        var links: List<Link>? = null

        @ElementList(name = "item", required = true, inline = true, type = Item::class)
        var itemList: List<Item>? = null

        @Element(required = false)
        var image: Image? = null

        @Element
        var title: String? = null

        @Element(required = false)
        var description: String? = null

        @Element
        var language: String? = null
        @Element(name = "pubDate", required = false)
        var pubDate: String? = null
        @Element(name = "ttl", required = false)
        var ttl: Int = 0

        override fun toString(): String {
            return "Channel{" +
                    "links=" + links +
                    ", itemList=" + itemList +
                    ", title='" + title + '\'' +
                    ", language='" + language + '\'' +
                    ", ttl=" + ttl +
                    ", pubDate='" + pubDate + '\'' +
                    '}'
        }

        @Root(name = "image", strict = false)
        class Image {
            @Attribute(required = false)
            var url: String? = null
            @Attribute(required = false)
            var title: String? = null
            @Attribute(required = false)
            var link: String? = null
            @Attribute(required = false)
            var width: String? = null
            @Attribute(required = false)
            var height: String? = null
        }

        class Link {
            @Attribute(required = false)
            var href: String? = null

            @Attribute(required = false)
            var rel: String? = null

            @Attribute(name = "type", required = false)
            var contentType: String? = null

            @Text(required = false)
            var link: String? = null
        }

        @Root(name = "item", strict = false)
        class Item {

            @Element(name = "title", required = true)
            var title: String? = null
            @Element(name = "link", required = true)
            var link: String? = null
            @Element(name = "description", required = false)
            var description: String? = null
            @Element(name = "author", required = false)
            var author: String? = null
            @ElementList(name = "category", required = false, inline = true, type = String::class)
            var categories: List<String>? = null
            @Element(name = "comments", required = false)
            var comments: String? = null
            @Element(name = "enclosure", required = false)
            var enclosure: String? = null
            @Element(name = "guid", required = false)
            var guid: String? = null
            @Element(name = "pubDate", required = false)
            var pubDate: String? = null
            @Element(name = "source", required = false)
            var source: String? = null

            override fun toString(): String {
                return "Item{" +
                        "title='" + title + '\'' +
                        ", link='" + link + '\'' +
                        ", description='" + description + '\'' +
                        ", author='" + author + '\'' +
                        ", category='" + categories + '\'' +
                        ", comments='" + comments + '\'' +
                        ", enclosure='" + enclosure + '\'' +
                        ", guid='" + guid + '\'' +
                        ", pubDate='" + pubDate + '\'' +
                        ", source='" + source + '\'' +
                        '}'
            }
        }
    }
}