package xyz.jbapps.vplan.util.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.util.Map;

import xyz.jbapps.vplan.data.FHGFeed;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;

/**
 * Volley Request, provides FHG Feed
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
public class FeedRequest extends Request<FHGFeed> {

    private static final String TAG = "FeedRequest";

    private final Response.Listener<FHGFeed> listener;

    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    public FeedRequest(String url, Response.Listener<FHGFeed> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected void deliverResponse(FHGFeed response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<FHGFeed> parseNetworkResponse(NetworkResponse response) {
        try {
            FHGFeed feed = new FHGFeed();
            feed.lastUpdated = HttpHeaderParser.parseDateAsEpoch(response.headers.get(HEADER_LAST_MODIFIED));
            FHGFeedXmlParser feedParser = new FHGFeedXmlParser();
            feed.feedItems = feedParser.parse(new ByteArrayInputStream(response.data));

            return Response.success(feed, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
