package de.jbapps.vplan.util;

import android.content.Context;
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

import de.jbapps.vplan.data.VPlanSet;

/**
 * This class manages the whole loading and caching process for the MainActivity.
 * Some work is delegated to other classes.
 */
public class VPlanProvider {

    private static final String TAG = "VPlanProvider";
    /**
     * receives callbacks for specific actions
     */
    private final IVPlanLoader mListener;
    private VPlanSet mVPlanSet;
    private VPlanLoader mVPlanLoader;

    public VPlanProvider(Context context, IVPlanLoader listener) {
        mVPlanSet = new VPlanSet(context);
        mListener = listener;
    }

    public void cancel() {
        if (mVPlanLoader != null) {
            mVPlanLoader.cancel(true);
            mVPlanLoader = null;
        }
    }

    public void getVPlan(boolean forceLoad) {
        cancel();
        mVPlanLoader = new VPlanLoader(mVPlanSet);
        mVPlanLoader.execute(forceLoad);
    }

    public void getCachedVPlan() {
        if (mVPlanSet.readVPlan()) {
            mListener.vPlanLoaded(mVPlanSet);
        } else {
            mListener.vPlanLoaded(null);
            Log.w(TAG, "VPlanSet empty!");
        }
    }

    public void loaderFinished(boolean loadCache) {
        mVPlanLoader = null;
        if (loadCache) {
            getCachedVPlan();
        } else {

            mListener.vPlanLoaded(mVPlanSet);
        }
    }

    /**
     * This interface is the connection to the given Activity
     */
    public interface IVPlanLoader {
        void vPlanLoaded(VPlanSet vplanset);
    }

    protected class VPlanLoader extends AsyncTask<Boolean, Void, Void> {

        public static final String JSON_MOTD = "motd";
        public static final String JSON_HEADER = "header";
        public static final String JSON_HEADER_TITLE = "header_title";
        public static final String JSON_HEADER_STATUS = "header_status";
        public static final String JSON_VPLAN = "vplan";
        public static final String JSON_VPLAN_SUBJECT = "subject";
        public static final String JSON_VPLAN_OMITTED = "omitted";
        public static final String JSON_VPLAN_HOUR = "hour";
        public static final String JSON_VPLAN_ROOM = "room";
        public static final String JSON_VPLAN_CONTENT = "content";
        public static final String JSON_VPLAN_GRADE = "grade";
        public static final String JSON_VPLAN_MARKED_NEW = "marked_new";

        private static final String TAG = "VPlanLoader";
        private static final String URL_VPLAN1 = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
        private static final String URL_VPLAN2 = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

        private HttpClient mClient;
        private VPlanSet mVPlanSet;
        private boolean loadCache = false;

        public VPlanLoader(VPlanSet vPlanSet) {
            mClient = new DefaultHttpClient();
            mVPlanSet = vPlanSet;
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            boolean forceLoad = params[0];
            try {
                //Load the headers first to check if cache is up to date
                if (!forceLoad && mVPlanSet.readHeader()) {
                    String header1 = loadHeader(URL_VPLAN1);
                    String header2 = loadHeader(URL_VPLAN2);

                    if (mVPlanSet.getHeader1().equals(header1) && mVPlanSet.getHeader2().equals(header2)) {
                        loadCache = true;
                        return null;
                    }
                }
                HttpResponse res1 = loadPage(URL_VPLAN1);
                mVPlanSet.setVPlan1(parse(getVPlan(res1)));
                String header1 = getHeader(res1);

                HttpResponse res2 = loadPage(URL_VPLAN2);
                mVPlanSet.setVPlan2(parse(getVPlan(res2)));
                String header2 = getHeader(res2);
                if (mVPlanSet.readHeader()) {
                    if (!(mVPlanSet.getHeader1().equals(header1) && mVPlanSet.getHeader2().equals(header2))) {
                        Log.i(TAG, "headers different, trigger executed");
                        //TODO: VPlan updated: notify cloud
                        //API_v1.doTrigger2("");
                        //new CloudUpdater().execute(mVPlanSet.getHeader1(), mVPlanSet.getHeader2());
                    }
                }

                mVPlanSet.setHeader1(header1);
                mVPlanSet.setHeader2(header2);
                mVPlanSet.writeAll();

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String loadHeader(String url) throws IOException {
            HttpHead httpHead = new HttpHead(url);
            HttpResponse response = mClient.execute(httpHead);
            return getHeader(response);
        }

        private HttpResponse loadPage(String url) throws IOException {
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

        private String getHeader(HttpResponse response) {
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().contains("Last-Modified")) {
                    Log.i(TAG, header.getName() + " : " + header.getValue());
                    return header.getValue();
                }
            }
            return null;
        }

        private JSONObject parse(String vplan) throws JSONException {
            JSONObject jPlan = new JSONObject();
            Document doc = Jsoup.parse(vplan);
            JSONArray temp = new JSONArray();

            //get date and day name
            JSONObject header = new JSONObject();
            try {
                String status = vplan.split("</head>")[1];
                status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
                header.put(JSON_HEADER_STATUS, status);
                header.put(JSON_HEADER_TITLE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                header.put(JSON_HEADER_STATUS, "The VPlan-file is corrupted.");
                header.put(JSON_HEADER_TITLE, "~ please contact the support");
            }
            jPlan.put(JSON_HEADER, header);

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
            jPlan.put(JSON_MOTD, temp);
            temp = new JSONArray();

            //get vplan rows
            Elements listRows = doc.getElementsByClass("list").select("tr");
            for (Element line : listRows) {
                Elements cells = line.children();
                if (cells.select("th").size() == 0) {
                    JSONObject row = new JSONObject();
                    row.put(JSON_VPLAN_SUBJECT, cells.get(3).text());
                    row.put(JSON_VPLAN_HOUR, cells.get(1).text());
                    row.put(JSON_VPLAN_GRADE, cells.get(0).text());
                    row.put(JSON_VPLAN_CONTENT, cells.get(2).text());
                    row.put(JSON_VPLAN_ROOM, cells.get(4).text());
                    row.put(JSON_VPLAN_OMITTED, cells.get(5).text().contains("x"));
                    row.put(JSON_VPLAN_MARKED_NEW, cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
                    //Log.d(TAG, "" + cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
                    temp.put(row);
                }
            }
            jPlan.put(JSON_VPLAN, temp);
            Log.i(TAG, "VPlan parsed");
            return jPlan;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (mListener != null) {
                loaderFinished(loadCache);
            }
        }
    }

}
