package de.fhg_radolfzell.android_app.main.feed;

import de.fhg_radolfzell.android_app.data.Channel;
import de.fhg_radolfzell.android_app.data.Feed;
import de.fhg_radolfzell.android_app.data.Post;
import de.fhg_radolfzell.android_app.data.RSS;

/**
 * @author Jannik
 * @version 30.07.2016.
 */
public class FeedDataWrapperImpl {

    public static final int TYPE_ROW = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;
    private static final String TAG = "FeedDataWrapperImpl";

    private FeedElement[] elements;

    public void setData(RSS rssFeed) {

        if (rssFeed == null || rssFeed.getChannel() == null) {
            elements = new FeedElement[1];
            elements[0] = new FeedFooter();
            return;
        }
        Channel channel = rssFeed.getChannel();
        int length = 2 + (channel.itemList == null ? 0 : channel.itemList.size()); // header & footer + items

        elements = new FeedElement[length];
        FeedHeader h = new FeedHeader();
        h.date = channel.pubDate;
        h.title = channel.title;
        h.icon = channel.image.url;
        h.subtitle = channel.description;
        elements[0] = h;

        for (int i = 0; i < channel.itemList.size(); i++) {
            FeedItem r = new FeedItem();
            Channel.Item e = channel.itemList.get(i);
            r.title = e.title;
            r.author = e.author;
            r.content = e.description;
            r.link = e.link;
            r.date = e.pubDate;
            elements[i + 1] = r;
        }
        FeedFooter f = new FeedFooter();
        f.date = channel.pubDate;
        elements[length - 1] = f;
    }

    public void setData(Feed atomFeed) {
        if (atomFeed == null || atomFeed.entries == null) {
            elements = new FeedElement[1];
            elements[0] = new FeedFooter();
            return;
        }
        int length = 2 + atomFeed.entries.size(); // header & footer + items
        elements = new FeedElement[length];
        FeedHeader h = new FeedHeader();
        h.date = atomFeed.updated;
        h.title = atomFeed.title;
        h.icon = atomFeed.icon;
        h.subtitle = atomFeed.subTitle;
        elements[0] = h;

        for (int i = 0; i < atomFeed.entries.size(); i++) {
            FeedItem r = new FeedItem();
            Feed.Entry e = atomFeed.entries.get(i);
            r.title = e.title;
            r.author = e.author.name;
            r.content = e.summary;
            r.link = e.link.href;
            r.date = e.published;
            elements[i + 1] = r;
        }
        FeedFooter f = new FeedFooter();
        f.date = atomFeed.updated;
        elements[length - 1] = f;
    }

    public void setData(Post[] posts) {
        if (posts == null) return; //FIXME error
        elements = new FeedElement[posts.length];
        for (int i = 0; i < posts.length; i++) {
            FeedItem feedItem = new FeedItem();
            Post p = posts[i];
            feedItem.title = p.title.rendered;
            feedItem.author = "author";
            feedItem.content = p.excerpt.rendered;
            feedItem.link = p.link;
            feedItem.date = p.dateGmt;
            elements[i] = feedItem;
        }
    }

    public int getItemTypeAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        FeedElement e = elements[position];
        if (e == null) {
            throw new NullPointerException("null element in Array");
        }
        return e.type;
    }

    public FeedElement getItemAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        return elements[position];
    }

    public int length() {
        return elements == null ? 0 : elements.length;
    }

    public class FeedElement {
        public static final int TYPE_ROW = 0;
        public static final int TYPE_HEADER = 1;
        public static final int TYPE_FOOTER = 2;

        public final int type;

        public FeedElement(int type) {
            this.type = type;
        }
    }

    public class FeedFooter extends FeedElement {
        public String date;

        public FeedFooter() {
            super(FeedElement.TYPE_FOOTER);
        }
    }

    public class FeedHeader extends FeedElement {

        public String date;
        public String title;
        public String subtitle;
        public String icon;

        public FeedHeader() {
            super(FeedElement.TYPE_HEADER);
        }
    }

    public class FeedItem extends FeedElement {

        public String author;
        public String title;
        public String link;
        //        public String id;
        public String date;
        //        public List<String> categories;
        public String content;

        public FeedItem() {
            super(FeedElement.TYPE_ROW);
        }
    }


}
