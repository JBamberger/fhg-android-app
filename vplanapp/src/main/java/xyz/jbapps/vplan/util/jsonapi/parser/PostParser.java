package xyz.jbapps.vplan.util.jsonapi.parser;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import xyz.jbapps.vplan.util.jsonapi.data.PostItem;

@Deprecated
public class PostParser {

    public List<PostItem> parse(InputStream in, List<PostItem> itemCollection) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPostItemArray(reader, itemCollection);
        } finally {
            reader.close();
        }

    }

    public List<PostItem> readPostItemArray(JsonReader reader, List<PostItem> items) throws IOException {

        reader.beginArray();
        while (reader.hasNext()) {
            items.add(readPost(reader));
        }
        reader.endArray();
        return items;
    }

    public PostItem readPost(JsonReader reader) throws IOException {
        PostItem item = new PostItem();
        reader.beginObject();
        while (reader.hasNext()) {
            String tag = reader.nextName();
            switch (tag) {
                case "id":
                    item.id = reader.nextString();
                    break;
                case "date":
                    item.date = reader.nextString();
                    break;
                case "date_gmt":
                    item.date_gmt = reader.nextString();
                    break;
                case "modified":
                    item.modified = reader.nextString();
                    break;
                case "modified_gmt":
                    item.modified_gmt = reader.nextString();
                    break;
                case "slug":
                    item.slug = reader.nextString();
                    break;
                case "link":
                    item.link = reader.nextString();
                    break;
                case "title":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("rendered")) {
                            item.title = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "content":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("rendered")) {
                            item.content = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "excerpt":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("rendered")) {
                            item.excerpt = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "author":
                    item.author = reader.nextString();
                    break;
                case "featured_media":
                    item.featured_media = reader.nextString();
                    break;
                case "comment_status":
                    item.comment_status = reader.nextString();
                    break;
                case "ping_status":
                    item.ping_status = reader.nextString();
                    break;
                case "sticky":
                    item.sticky = reader.nextBoolean();
                    break;
                case "format":
                    item.format = reader.nextString();
                    break;
                case "categories":
                    ArrayList<String> categories = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        categories.add(reader.nextString());
                    }
                    reader.endArray();
                    item.categories = categories.toArray(new String[categories.size()]);
                    break;
                case "tags":
                    ArrayList<String> tags = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        tags.add(reader.nextString());
                    }
                    reader.endArray();
                    item.tags = tags.toArray(new String[tags.size()]);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return item;
    }
}
