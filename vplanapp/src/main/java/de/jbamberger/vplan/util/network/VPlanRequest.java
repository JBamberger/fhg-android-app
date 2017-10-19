package de.jbamberger.vplan.util.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

import de.jbamberger.vplan.data.VPlanData;
import de.jbamberger.vplan.util.VPlanParser;

public class VPlanRequest extends Request<VPlanData> {

    private final Response.Listener<VPlanData> listener;

    /**
     * http header last modified
     */
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    public VPlanRequest(String url, Response.Listener<VPlanData> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected void deliverResponse(VPlanData response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<VPlanData> parseNetworkResponse(NetworkResponse response) {
        try {
            long lastModified = HttpHeaderParser.parseDateAsEpoch(response.headers.get(HEADER_LAST_MODIFIED));
            String content = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            VPlanData data = VPlanParser.parse(content, lastModified);

            return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}