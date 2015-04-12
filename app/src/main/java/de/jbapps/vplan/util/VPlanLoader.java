package de.jbapps.vplan.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VPlanLoader extends AsyncTask<Boolean, Void, Void> {

    private static final String TAG = "VPlanLoader";

    private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    public static final String HEADER = "header";
    public static final String MOTD = "motd";
    public static final String VPLAN = "vplan";

    public static final String HEADER_TITLE = "header_title";
    public static final String HEADER_STATUS = "header_status";

    public static final String SUBJECT = "subject";
    public static final String OMITTED = "omitted";
    public static final String HOUR = "hour";
    public static final String ROOM = "room";
    public static final String CONTENT = "content";
    public static final String GRADE = "grade";


    IOnFinishedLoading mListener;
    HttpClient mClient;
    boolean onlyHeader;
    Header[] vPlanHeader1;
    Header[] vPlanHeader2;
    JSONObject vPlan1;
    JSONObject vPlan2;

    public VPlanLoader(IOnFinishedLoading listener) {
        mClient = new DefaultHttpClient();
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        onlyHeader = params[0];
        try {
            if (onlyHeader) {
                Log.i(TAG, "Loading VPlanHeader");
                vPlanHeader1 = hHead(VPLAN1_URL);
                vPlanHeader2 = hHead(VPLAN2_URL);
            } else {
                Log.i(TAG, "Loading VPlan");
                HttpResponse res1 = hGET(VPLAN1_URL);
                vPlanHeader1 = getHeader(res1);
                String vp1 = getVPlan(res1);
                if (vp1 != null) {
                    vPlan1 = parse(vp1);
                } else {
                    Log.wtf(TAG, "response1 == null");
                    throw new ClientProtocolException("Empty response content");
                }

                HttpResponse res2 = hGET(VPLAN2_URL);
                vPlanHeader2 = getHeader(res2);
                String vp2 = getVPlan(res2);
                if (vp2 != null) {
                    vPlan2 = parse(vp2);
                } else {
                    Log.wtf(TAG, "response2 == null");
                    throw new ClientProtocolException("Empty response content");
                }

            }
        } catch (ClientProtocolException | JSONException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Header[] hHead(String url) throws IOException {
        HttpHead httpHead = new HttpHead(url);
        HttpResponse response = mClient.execute(httpHead);
        return getHeader(response);
    }

    private HttpResponse hGET(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return mClient.execute(httpGet);
    }

    private String getVPlan(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            Log.i(TAG, "VPlan loaded");
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }

    private Header[] getHeader(HttpResponse response) {
        Header[] cleanHeaders = new Header[2];
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().contains("Last-Modified")) {
                cleanHeaders[0] = header;
                Log.i(TAG, header.getName() + " : " + header.getValue());
            }
            if (header.getName().contains("Content-Length")) {
                cleanHeaders[1] = header;
                Log.i(TAG, header.getName() + " : " + header.getValue());
            }
        }
        return cleanHeaders;
    }

    private JSONObject parse(String vplan) throws JSONException {
        JSONObject jPlan = new JSONObject();
        Document doc = Jsoup.parse(vplan);
        JSONArray temp = new JSONArray();

        //get date and day name
        JSONObject header = new JSONObject();
        try{
            String status = vplan.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            header.put(HEADER_STATUS, status);
            header.put(HEADER_TITLE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());
        }catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            header.put(HEADER_STATUS, "The VPlan-file is corrupted.");
            header.put(HEADER_TITLE, "~ please contact the support");
        }
        jPlan.put(HEADER, header);

        //get "Nachrichten zum Tag"
        Elements infoRows = doc.getElementsByClass("info").select("tr");

        for (Element line : infoRows) {
            Elements cells = line.children().select("td");

            JSONArray temp2 = new JSONArray();
            for (Element cell : cells) {
                temp2.put(cell.text());
            }
            try {
                temp2.get(0);
                temp.put(temp2);
            } catch (JSONException e) {
                Log.w(TAG, "HTML to JSON: Unknown empty element discarded...");
            }
        }
        jPlan.put(MOTD, temp);
        temp = new JSONArray();

        //get vplan rows
        Elements listRows = doc.getElementsByClass("list").select("tr");
        for (Element line : listRows) {
            Elements cells = line.children();
            if (cells.select("th").size() == 0) {
                JSONObject row = new JSONObject();
                row.put(SUBJECT, cells.get(3).text());
                row.put(HOUR, cells.get(1).text());
                row.put(GRADE, cells.get(0).text());
                row.put(CONTENT, cells.get(2).text());
                row.put(ROOM, cells.get(4).text());
                row.put(OMITTED, cells.get(5).text().contains("x"));
                temp.put(row);
            }
        }
        jPlan.put(VPLAN, temp);
        Log.i(TAG, "VPlan parsed");
        return jPlan;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mListener != null) {
            if (onlyHeader) {
                Log.d(TAG, "onVPlanHeaderLoaded() executed");
                if (vPlanHeader1 != null && vPlanHeader2 != null) {
                    mListener.onVPlanHeaderLoaded(vPlanHeader1, vPlanHeader2);
                } else {
                    mListener.onVPlanHeaderLoadingFailed();
                }
            } else {
                Log.d(TAG, "onVPlanLoaded() executed");
                Log.d(TAG, "#############################################################################################################################");
                if (vPlan1 != null && vPlan2 != null && vPlanHeader1 != null && vPlanHeader2 != null) {
                    mListener.onVPlanLoaded(vPlan1, vPlan2, vPlanHeader1, vPlanHeader2);
                } else {
                    mListener.onVPlanLoadingFailed();
                }
            }
        }
    }

    public interface IOnFinishedLoading {
        void onVPlanHeaderLoaded(Header[] vPlanHeader1, Header[] vPlanHeader2);

        void onVPlanHeaderLoadingFailed();

        void onVPlanLoaded(JSONObject vPlan1, JSONObject vPlan2, Header[] vPlanHeader1, Header[] vPlanHeader2);

        void onVPlanLoadingFailed();
    }
}