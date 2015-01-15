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

public class VPlanLoader2 extends AsyncTask<Boolean, Void, Void> {

    private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    private static final String DATE = "date";
    private static final String MOTD = "motd";
    private static final String VPLAN = "vplan";

    private static final String SUBJECT = "subject";
    private static final String OMITTED = "omitted";
    private static final String HOUR = "hour";
    private static final String ROOM = "room";
    private static final String CONTENT = "content";
    private static final String GRADE = "grade";


    IOnFinishedLoading mListener;
    HttpClient mClient;
    boolean onlyHeader;
    Header[] vPlanHeader1;
    Header[] vPlanHeader2;
    JSONObject vPlan1;
    JSONObject vPlan2;

    public VPlanLoader2(IOnFinishedLoading listener) {
        mClient = new DefaultHttpClient();
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        onlyHeader = params[0];
        try {
            if (onlyHeader) {
                load(VPLAN1_URL, vPlanHeader1);
                load(VPLAN2_URL, vPlanHeader2);
            } else {
                load(VPLAN1_URL, vPlan1, vPlanHeader1);
                load(VPLAN2_URL, vPlan2, vPlanHeader2);
            }
        } catch (ClientProtocolException | JSONException e) {
            Log.d("VPlanLoader: EXCEPTION", e.getMessage());//TODO: notify User maybe...
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void load(String url, Header[] header) throws IOException, JSONException {
        HttpHead httpHead = new HttpHead(url);
        HttpResponse response = mClient.execute(httpHead);
        header = getHeader(response);
    }

    private void load(String url, JSONObject vplan, Header[] header) throws IOException, JSONException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = mClient.execute(httpGet);
        header = getHeader(response);
        String vPlan = getVPlan(response);

        if (vPlan != null) {
            vplan = parse(vPlan);
        } else {
            //should never happen...
            throw new ClientProtocolException("Empty response content");
        }
    }

    private String getVPlan(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();

            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }

    private Header[] getHeader(HttpResponse response) {
        Header[] cleanHeaders = new Header[2];
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            System.out.println("Key : " + header.getName() + " ,Value : " + header.getValue());
            if (header.getName().contains("Last-Mofified")) {
                cleanHeaders[0] = header;
            }
            if (header.getName().contains("Content-Length")) {
                cleanHeaders[1] = header;
            }
        }
        return cleanHeaders;
    }

    private JSONObject parse(String vplan) throws JSONException {
        JSONObject jPlan = new JSONObject();
        Document doc = Jsoup.parse(vplan);
        JSONArray temp = new JSONArray();

        //get date and day name
        jPlan.put(DATE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());

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
                Log.d("HTML to JSON", "Unknown empty element discarded...");
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
                row.put(OMITTED, Boolean.toString(cells.get(5).text().contains("x")));
                temp.put(row);
            }
        }
        jPlan.put(VPLAN, temp);

        return jPlan;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mListener != null) {
            if (onlyHeader) {
                if (vPlanHeader1 != null && vPlanHeader2 != null) {
                    mListener.onVPlanHeaderLoaded(vPlanHeader1, vPlanHeader2);
                } else {
                    mListener.onVPlanHeaderLoadingFailed();
                }
            } else {
                if (vPlan1 != null && vPlan2 != null && vPlanHeader1 != null && vPlanHeader2 != null) {
                    mListener.onVPlanLoaded(vPlan1, vPlan2, vPlanHeader1, vPlanHeader2);
                } else {
                    mListener.onVPlanLoadingFailed();
                }
            }
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanHeaderLoaded(Header[] vPlanHeader1, Header[] vPlanHeader2);

        public void onVPlanHeaderLoadingFailed();

        public void onVPlanLoaded(JSONObject vPlan1, JSONObject vPlan2, Header[] vPlanHeader1, Header[] vPlanHeader2);

        public void onVPlanLoadingFailed();
    }
}