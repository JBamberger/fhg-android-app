package xyz.jbapps.vplan.util.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.data.VPlanRow;

public class VPlanRequest extends Request<VPlanData> {

    private static final String TAG = "VPlanRequest";

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
            long header = HttpHeaderParser.parseDateAsEpoch(response.headers.get(HEADER_LAST_MODIFIED));
            String content = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            VPlanData vData = new VPlanData();
            vData.setLastUpdated(header);
            Document doc = Jsoup.parse(content);
            vData.setStatus(readVPlanStatus(content));
            vData.setTitle(readVPlanTitle(doc));
            vData.setMotd(readMotdTable(doc.getElementsByClass("info")));
            readVPlanTable(doc.getElementsByClass("list").select("tr"), vData);
            return Response.success(vData, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }


    @NonNull
    private String readVPlanStatus(String vplan) {
        try {
            String status = vplan.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            return status;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "VPlan file is corrupted, an error occurred...";
        }
    }

    @NonNull
    private String readVPlanTitle(Document vplanDoc) {
        return vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text();

    }

    @NonNull
    private String readMotdTable(Elements classInfo) {
        Elements tableRows = classInfo.select("tr");
        StringBuilder motd = new StringBuilder();
        for (Element line : tableRows) {
            Elements cells = line.children().select("td");
            int size = cells.size();
            if (size == 0) {
                continue;
            }
            boolean highlightRow = size > 1 && cells.first().text().toLowerCase().contains("unterrichtsfrei");
            if (highlightRow) {
                motd.append("<font color=#FF5252>");
            }
            for (int i = 0; i < size; i++) {
                Element cell = cells.get(i);
                motd.append(cell.toString());
                if (i < size - 2) {
                    motd.append(" | ");
                }
            }
            if (highlightRow) {
                motd.append("</font>");
            }
            motd.append("<br />");
        }
        String data = motd.toString();
        Log.d(TAG, data);
        Document dat = Jsoup.parse(data);
        dat.select("html").unwrap();
        dat.select("head").unwrap();
        dat.select("body").unwrap();
        dat.select("td").unwrap();
        data = dat.toString();
        Log.d(TAG, data);
        return data;

    }

    private void readVPlanTable(Elements vPlanTable, VPlanData vData) {
        for (Element line : vPlanTable) {
            Elements cells = line.children();
            VPlanRow row = readVPlanTableCells(cells);
            if (row != null) {
                vData.addVPlanRow(row);
            }
        }
    }

    @Nullable
    private VPlanRow readVPlanTableCells(Elements cells) {
        VPlanRow row = new VPlanRow();
        if (cells.select("th").size() == 0 && cells.size() >= 6) {
            row.setSubject(cells.get(3).text());
            row.setHour(cells.get(1).text());
            row.setGrade(cells.get(0).text());
            row.setContent(cells.get(2).text());
            row.setRoom(cells.get(4).text());
            row.setOmitted(cells.get(5).text().contains("x"));
            row.setMarkedNew(cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
            return row;
        } else {
            Log.i(TAG, "Given data contains table head");
            return null;
        }
    }

}