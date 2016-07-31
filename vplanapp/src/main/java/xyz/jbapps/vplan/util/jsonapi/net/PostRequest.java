package xyz.jbapps.vplan.util.jsonapi.net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.jbapps.vplan.data.FHGFeed;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;
import xyz.jbapps.vplan.util.jsonapi.parser.PostParser;

/**
 * Volley Request, provides Posts from fhg-radolfzell.de
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
@Deprecated
public class PostRequest extends Request<List<PostItem>> {

    private static final String TAG = "PostRequest";

    private final Response.Listener<List<PostItem>> listener;

    public PostRequest(String url, Response.Listener<List<PostItem>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected void deliverResponse(List<PostItem> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<PostItem>> parseNetworkResponse(NetworkResponse response) {
        try {
            List<PostItem> PostItems = new ArrayList<>();
            PostParser PostParser = new PostParser();
            PostParser.parse(new ByteArrayInputStream(response.data), PostItems);
            return Response.success(PostItems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
