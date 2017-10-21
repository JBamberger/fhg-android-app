package de.fhg_radolfzell.android_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class Post {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("date_gmt")
    @Expose
    public String dateGmt;
    @SerializedName("modified_gmt")
    @Expose
    public String modifiedGmt;
    @SerializedName("slug")
    @Expose
    public String slug;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("title")
    @Expose
    public Title title;
    @SerializedName("content")
    @Expose
    public Content content;
    @SerializedName("excerpt")
    @Expose
    public Excerpt excerpt;
    @SerializedName("featured_media")
    @Expose
    public int featuredMedia;
    @SerializedName("meta")
    @Expose
    public List<Object> meta = null;
    @SerializedName("_links")
    @Expose
    public Links links;

    public class Guid {

        @SerializedName("rendered")
        @Expose
        public String rendered;

    }

    public class Title {

        @SerializedName("rendered")
        @Expose
        public String rendered;

    }

    public class Content {

        @SerializedName("rendered")
        @Expose
        public String rendered;

    }

    public class Excerpt {

        @SerializedName("rendered")
        @Expose
        public String rendered;

    }

    public class Links {

        @SerializedName("self")
        @Expose
        public List<Self> self = null;
        @SerializedName("wp:featuredmedia")
        @Expose
        public List<WpFeaturedmedium> wpFeaturedmedia = null;
        @SerializedName("wp:attachment")
        @Expose
        public List<WpAttachment> wpAttachment = null;


        public class Self {

            @SerializedName("href")
            @Expose
            public String href;

        }

        public class WpFeaturedmedium {

            @SerializedName("embeddable")
            @Expose
            public Boolean embeddable;
            @SerializedName("href")
            @Expose
            public String href;

        }

        public class WpAttachment {

            @SerializedName("href")
            @Expose
            public String href;

        }
    }








}


