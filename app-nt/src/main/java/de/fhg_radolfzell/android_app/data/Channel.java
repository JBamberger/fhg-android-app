package de.fhg_radolfzell.android_app.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom")
})
@Root(strict = false)
public class Channel {
    // Tricky part in Simple XML because the link is named twice
    @ElementList(entry = "link", inline = true, required = false, type = Link.class)
    public List<Link> links;

    @ElementList(name = "item", required = true, inline = true, type = Item.class)
    public List<Item> itemList;

    @Element(required = false)
    public Image image;

    @Element
    public String title;

    @Element(required = false)
    public String description;

    @Element
    public String language;
    @Element(name = "pubDate", required = false)
    public String pubDate;
    @Element(name = "ttl", required = false)
    int ttl;

    @Override
    public String toString() {
        return "Channel{" +
                "links=" + links +
                ", itemList=" + itemList +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", ttl=" + ttl +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }

    @Root(name = "image", strict = false)
    public static class Image {
        @Attribute(required = false)
        public String url;
        @Attribute(required = false)
        public String title;
        @Attribute(required = false)
        public String link;
        @Attribute(required = false)
        public String width;
        @Attribute(required = false)
        public String height;
    }

    public static class Link {
        @Attribute(required = false)
        public String href;

        @Attribute(required = false)
        public String rel;

        @Attribute(name = "type", required = false)
        public String contentType;

        @Text(required = false)
        public String link;
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Element(name = "title", required = true)
        public String title;
        @Element(name = "link", required = true)
        public String link;
        @Element(name = "description", required = false)
        public String description;
        @Element(name = "author", required = false)
        public String author;
        @ElementList(name = "category", required = false, inline = true, type = String.class)
        public List<String> categories;
        @Element(name = "comments", required = false)
        public String comments;
        @Element(name = "enclosure", required = false)
        public String enclosure;
        @Element(name = "guid", required = false)
        public String guid;
        @Element(name = "pubDate", required = false)
        public String pubDate;
        @Element(name = "source", required = false)
        public String source;

        @Override
        public String toString() {
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
                    '}';
        }
    }
}