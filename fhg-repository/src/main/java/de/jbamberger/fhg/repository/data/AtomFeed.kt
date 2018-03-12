package de.jbamberger.api.data

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com
 */
@Root(strict = false)
internal class AtomFeed {

    @Element(name = "title")
    var title: String? = null

    @Element(name = "subtitle")
    var subtitle: String? = null

    @Element(name = "updated")
    var updated: String? = null

    @Element(name = "icon")
    var icon: String? = null

    @ElementList(name = "entry", inline = true, type = Entry::class)
    var entries: List<Entry>? = null

    @Root(name = "entry")
    class Entry {
        @Element(name = "author")
        var author: Author? = null
        @Element(name = "title")
        var title: String? = null
        @Element(name = "link")
        var link: Link? = null
        @Element(name = "id")
        var id: String? = null
        @Element(name = "updated")
        var updated: String? = null
        @Element(name = "published")
        var published: String? = null
        @ElementList(name = "category", type = Category::class, required = false, inline = true)
        var categories: List<Category>? = null
        @Element(name = "summary")
        var summary: String? = null

        class Author {
            @Element(name = "name")
            var name: String? = null
        }
    }

    class Category {
        @Attribute(name = "scheme")
        var scheme: String? = null
        @Attribute(name = "term")
        var term: String? = null
    }

    class Link {
        @Attribute(name = "rel")
        var rel: String? = null
        @Attribute(name = "type")
        var type: String? = null
        @Attribute(name = "href")
        var href: String? = null
    }
}
