package xyz.jbapps.vplan.util.jsonapi.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.jbapps.vplan.util.FHGFeedXmlParser;
import xyz.jbapps.vplan.util.jsonapi.data.MediaItem;
import xyz.jbapps.vplan.util.jsonapi.parser.MediaParser;

@Deprecated
public class MediaRequest extends Request<List<MediaItem>> {

    private static final String TAG = "MediaRequest";

    private final Response.Listener<List<MediaItem>> listener;

    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    public MediaRequest(String url, Response.Listener<List<MediaItem>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected void deliverResponse(List<MediaItem> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<MediaItem>> parseNetworkResponse(NetworkResponse response) {
        try {
            //System.out.println("MediaRequest header last-modified: " + HttpHeaderParser.parseDateAsEpoch(response.headers.get(HEADER_LAST_MODIFIED)));
            List<MediaItem> mediaItems = new ArrayList<>();
            MediaParser mediaParser = new MediaParser();
            mediaParser.parse(new ByteArrayInputStream(response.data), mediaItems);
            return Response.success(mediaItems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
