/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package xyz.jbapps.vplan.util;

import android.util.Xml;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FHGFeedXmlParser {
    private static final String ns = null;
    private final SimpleDateFormat dateParser;
    private final SimpleDateFormat dateFormatter;

    public FHGFeedXmlParser() {
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'", Locale.US);
        this.dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);
    }

    public List<FHGFeedItem> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<FHGFeedItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<FHGFeedItem> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private FHGFeedItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = "";
        String author = "";
        String summary = "";
        String link = "";
        String updated_at = "";
        String published_at = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTitle(parser);
                    break;
                case "author":
                    author = readAuthor(parser);
                    break;
                case "summary":
                    summary = readSummary(parser);
                    break;
                case "id":
                    link = readId(parser);
                    break;
                case "updated":
                    updated_at = readUpdated(parser);
                    break;
                case "published":
                    published_at = readPublished(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        FHGFeedItem item = new FHGFeedItem(title, author, summary, link, updated_at, published_at);
        item.unescape();
        return item;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "author");
        String author = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "name":
                    parser.require(XmlPullParser.START_TAG, ns, "name");
                    author = readText(parser);
                    parser.require(XmlPullParser.END_TAG, ns, "name");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "author");
        return author;
    }

    private String readUpdated(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "updated");
        String updated = readText(parser);
        try {
            updated = dateFormatter.format(dateParser.parse(updated));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parser.require(XmlPullParser.END_TAG, ns, "updated");
        return updated;
    }

    private String readPublished(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "published");
        String published = readText(parser);
        try {
            published = dateFormatter.format(dateParser.parse(published));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parser.require(XmlPullParser.END_TAG, ns, "published");
        return published;
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        if (tag.equals("link")) {
            String relType = parser.getAttributeValue(null, "rel");
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * This object contains a single fhg feed item
     */
    public class FHGFeedItem {
        @SerializedName("feed_entry_title")
        public String title;
        @SerializedName("feed_entry_author")
        public String author;
        @SerializedName("feed_entry_link")
        public String link;
        @SerializedName("feed_entry_summary")
        public String summary;
        @SerializedName("feed_entry_updated_at")
        public String updated_at;
        @SerializedName("feed_entry_published_at")
        public String published_at;

        public void escape() {
            summary = StringEscapeUtils.escapeHtml4(summary);
            title = StringEscapeUtils.escapeHtml4(title);
            author = StringEscapeUtils.escapeHtml4(author);
        }

        public void unescape() {
            summary = StringEscapeUtils.unescapeHtml4(summary);
            title = StringEscapeUtils.unescapeHtml4(title);
            author = StringEscapeUtils.unescapeHtml4(author);
        }

        public FHGFeedItem(String title, String author, String summary, String link, String updated_at, String published_at) {
            this.title = title;
            this.author = author;
            this.summary = summary;
            this.link = link;
            this.updated_at = updated_at;
            this.published_at = published_at;
        }
    }
}
