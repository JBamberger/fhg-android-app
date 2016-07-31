package xyz.jbapps.vplan.util.jsonapi.data;

import android.os.Bundle;

@Deprecated
public class PostItem {
    public String id;
    public String date;
    public String date_gmt;
    public String modified;
    public String modified_gmt;
    public String slug;
    public String link;
    public String title;
    public String content;
    public String excerpt;
    public String author;
    public String featured_media;
    public String comment_status;
    public String ping_status;
    public boolean sticky;
    public String format;
    public String categories[];
    public String tags[];

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("date", date);
        bundle.putString("date_gmt", date_gmt);
        bundle.putString("modified", modified);
        bundle.putString("modified_gmt", modified_gmt);
        bundle.putString("slug", slug);
        bundle.putString("link", link);
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("excerpt", excerpt);
        bundle.putString("author", author);
        bundle.putString("featured_media", featured_media);
        bundle.putString("comment_status", comment_status);
        bundle.putString("ping_status", ping_status);
        bundle.putBoolean("sticky", sticky);
        bundle.putString("format", format);
        bundle.putStringArray("categories", categories);
        bundle.putStringArray("tags", tags);
        return bundle;
    }

    public static PostItem fromBundle(Bundle bundle) {
        PostItem postItem = new PostItem();
        postItem.id = bundle.getString("id");
        postItem.date = bundle.getString("date");
        postItem.date_gmt = bundle.getString("date_gmt");
        postItem.modified = bundle.getString("modified");
        postItem.modified_gmt = bundle.getString("modified_gmt");
        postItem.slug = bundle.getString("slug");
        postItem.link = bundle.getString("link");
        postItem.title = bundle.getString("title");
        postItem.content = bundle.getString("content");
        postItem.excerpt = bundle.getString("excerpt");
        postItem.author = bundle.getString("author");
        postItem.featured_media = bundle.getString("featured_media");
        postItem.comment_status = bundle.getString("comment_status");
        postItem.ping_status = bundle.getString("ping_status");
        postItem.sticky = bundle.getBoolean("sticky");
        postItem.format = bundle.getString("format");
        postItem.categories = bundle.getStringArray("categories");
        postItem.tags = bundle.getStringArray("tags");
        return postItem;
    }
}