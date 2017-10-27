package de.jbamberger.api.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
@Root(strict = false)
class AtomFeed {

    @Element(name = "title")
    public String title;

    @Element(name = "subtitle")
    public String subTitle;

    @Element(name = "updated")
    public String updated;

    @Element(name = "icon")
    public String icon;

    @ElementList(name = "entry", inline = true, type = Entry.class)
    public List<Entry> entries;

    @Root(name = "entry")
    public static class Entry {
        @Element(name = "author")
        public Author author;
        @Element(name = "title")
        public String title;
        @Element(name = "link")
        public Link link;
        @Element(name = "id")
        public String id;
        @Element(name = "updated")
        public String updated;
        @Element(name = "published")
        public String published;
        @ElementList(name = "category", type = Category.class, required = false, inline = true)
        public List<Category> categories;
        @Element(name = "summary")
        public String summary;

        public static class Author {
            @Element(name = "name")
            public String name;
        }

    }

    public static class Category {
        @Attribute(name = "scheme")
        public String scheme;
        @Attribute(name = "term")
        public String term;
    }

    public static class Link {
        @Attribute(name = "rel")
        public String rel;
        @Attribute(name = "type")
        public String type;
        @Attribute(name = "href")
        public String href;
    }
}
