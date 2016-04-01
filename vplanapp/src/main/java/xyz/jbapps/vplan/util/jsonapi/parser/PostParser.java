package xyz.jbapps.vplan.util.jsonapi.parser;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import xyz.jbapps.vplan.util.jsonapi.data.MediaItem;

public class PostParser {

    public List<MediaItem> parse(InputStream in, List<MediaItem> itemCollection) throws IOException{
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMediaItemArray(reader, itemCollection);
        } finally {
            reader.close();
        }

    }

    public List<MediaItem> readMediaItemArray(JsonReader reader, List<MediaItem> items) throws IOException {

        reader.beginArray();
        while (reader.hasNext()) {
            items.add(readMessage(reader));
        }
        reader.endArray();
        return items;
    }

    public MediaItem readMessage(JsonReader reader) throws IOException {
        String url = null;
        String title = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String tag = reader.nextName();
            switch (tag) {
                case "source_url":
                    url = reader.nextString();
                    break;
                case "title":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("rendered")) {
                            title = reader.nextString();
                        }else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new MediaItem(title, url);
    }
}
