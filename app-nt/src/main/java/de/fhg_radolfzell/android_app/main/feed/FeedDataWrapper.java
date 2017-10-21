package de.fhg_radolfzell.android_app.main.feed;

import de.fhg_radolfzell.android_app.data.Feed;
import de.fhg_radolfzell.android_app.data.RSS;

/**
 * @author Jannik
 * @version 30.07.2016.
 */
public abstract class FeedDataWrapper {

    private static final String TAG = "FeedDataWrapper";

    private FeedElement[] elements;

    public abstract void setData(RSS rssFeed);

    public abstract void setData(Feed atomFeed);

    public abstract int getItemTypeAtPosition(int position) throws ArrayIndexOutOfBoundsException;

    public abstract FeedElement getItemAtPosition(int position) throws ArrayIndexOutOfBoundsException;

    public abstract int length();

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
