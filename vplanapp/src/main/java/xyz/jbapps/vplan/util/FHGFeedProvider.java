package xyz.jbapps.vplan.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import xyz.jbapps.vplan.data.FHGFeed;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class FHGFeedProvider extends AsyncTask<Object, Object, Boolean> {

    public static final int TYPE_LOAD = 0;
    public static final int TYPE_CACHE = 1;
    public static final int TYPE_FORCE_LOAD = 2;
    private static final String TAG = "FHGFeedProvider";
    private static final boolean SUCCESS = true;
    private static final boolean FAILURE = false;
    private static final String FHG_FEED_URL = "http://www.fhg-radolfzell.de/feed/atom";
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private final IFHGFeedResultListener listener;
    private final PersistentCache persistentCache;
    private final SimpleDateFormat dateParser;
    private final int type;
    private FHGFeed fhgFeed;

    public FHGFeedProvider(Context context, int type, IFHGFeedResultListener listener) {
        this.dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        this.listener = listener;
        this.type = type;
        this.persistentCache = new PersistentCache(context);
    }

    private boolean typeCache() {
        try {
            fhgFeed = persistentCache.readFHGFeed(PersistentCache.FILE_FHG_FEED);
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return FAILURE;
        }
    }

    private boolean typeForceLoad() {
        try {
            fhgFeed = loadFeedFromNet();
            persistentCache.writeFHGFeed(fhgFeed, PersistentCache.FILE_FHG_FEED);
            return SUCCESS;
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
            return FAILURE;
        }
    }

    private boolean typeLoad() {
        try {
            FHGFeed cachedFeed = persistentCache.readFHGFeed(PersistentCache.FILE_FHG_FEED);
            long h1 = loadFeedHeaderFromNet();
            long h1_cache = cachedFeed.lastUpdated;
            if ((h1 == h1_cache)) {
                fhgFeed = cachedFeed;
                return SUCCESS;
            } else {
                return typeForceLoad();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return typeForceLoad();
        }
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        switch (type) {
            case TYPE_CACHE:
                return typeCache();
            case TYPE_LOAD:
                return typeLoad();
            case TYPE_FORCE_LOAD:
                return typeForceLoad();
            default:
                return FAILURE;
        }
    }

    @Override
    protected void onPostExecute(Boolean o) {
        if (listener != null) {
            if (o) {
                listener.feedLoadingSucceeded(fhgFeed.feedItems);
            } else {
                listener.feedLoadingFailed();
            }
        }
    }

    @Nullable
    private FHGFeed loadFeedFromNet() throws IOException, XmlPullParserException {
        FHGFeed feed = new FHGFeed();
        URL feedUrl = new URL(FHG_FEED_URL);
        HttpURLConnection connection = (HttpURLConnection) feedUrl.openConnection();
        try {
            connection.setRequestMethod("GET");
            Map<String, List<String>> headers = connection.getHeaderFields();
            feed.lastUpdated = getLastModified(headers);
            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                FHGFeedXmlParser feedParser = new FHGFeedXmlParser();
                feed.feedItems = feedParser.parse(in);
                return feed;
            } else {
                throw new IOException("Networking error");
            }
        } finally {
            connection.disconnect();
        }
    }

    private long loadFeedHeaderFromNet() throws IOException {
        URL feedUrl = new URL(FHG_FEED_URL);
        HttpURLConnection connection = (HttpURLConnection) feedUrl.openConnection();
        try {
            connection.setRequestMethod("HEAD");
            Map<String, List<String>> headers = connection.getHeaderFields();

            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                return getLastModified(headers);
            } else {
                throw new IOException("http response not ok; response code is: " + resCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    private long getLastModified(Map<String, List<String>> headers) throws IOException {
        if (headers.containsKey(HEADER_LAST_MODIFIED)) {
            String lastModified = headers.get(HEADER_LAST_MODIFIED).get(0);
            //Wed, 05 Aug 2015 10:21:47 GMT
            try {
                return dateParser.parse(lastModified).getTime();
            } catch (ParseException e) {
                throw new IOException("could not parse header date");
            }
        } else {
            throw new IOException("header " + HEADER_LAST_MODIFIED + " not in given data set");
        }
    }

    public interface IFHGFeedResultListener {

        void feedLoadingFailed();

        void feedLoadingSucceeded(List<FHGFeedXmlParser.FHGFeedItem> feed);
    }
}
