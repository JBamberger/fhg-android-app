package xyz.jbapps.vplan.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jannik Bamberger
 * @version 1.0
 *
 * The FHGFeedProvider loads and parses the FHG feed and returns the retrieved objects to the callback
 * */
public class FHGFeedProvider extends AsyncTask<Object, Object, List<FHGFeedXmlParser.FHGFeedItem>> {

    /**
     * url of the fhg feed
     */
    private static final String FHG_FEED_URL = "http://www.fhg-radolfzell.de/feed/atom";

    private IFHGFeedResultListener listener;

    public FHGFeedProvider(IFHGFeedResultListener listener) {
        this.listener = listener;
    }

    @Override
    @Nullable
    protected List<FHGFeedXmlParser.FHGFeedItem> doInBackground(Object... params) {
        List<FHGFeedXmlParser.FHGFeedItem> feed = null;
        try {
            feed = loadFeed();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return feed;
    }

    /**
     * This method loads the given url and returns the given document as {@link String}
     *
     * @return document as string
     * @throws IOException
     */
    @Nullable
    private List<FHGFeedXmlParser.FHGFeedItem> loadFeed() throws IOException, XmlPullParserException {
        URL url = new URL(FHG_FEED_URL);
        List<FHGFeedXmlParser.FHGFeedItem> feed = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                FHGFeedXmlParser feedParser = new FHGFeedXmlParser();
                feed = feedParser.parse(in);

                return feed;

            } else {
                throw new IOException("Networking error");
            }
        } finally {
            connection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(List<FHGFeedXmlParser.FHGFeedItem> feed) {
        super.onPostExecute(feed);
        if(listener != null) {
            if(feed != null && !feed.isEmpty()) {
                listener.feedLoadingSucceeded(feed);
            } else {
                listener.feedLoadingFailed();
            }
        }
    }


    public interface IFHGFeedResultListener {
        /**
         * If the loading process fails in some case the listener will be notified so the UI can be
         * updated accordingly.
         */
        void feedLoadingFailed();

        /**
         * If the loading process succeeded the listener will receive the feed data
         */
        void feedLoadingSucceeded(List<FHGFeedXmlParser.FHGFeedItem> feed);
    }
}
