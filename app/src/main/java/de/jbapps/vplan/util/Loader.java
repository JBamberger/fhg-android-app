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
 * This class manages the whole loading and caching process for the MainActivity
 */
public abstract class Loader {

    private static final String TAG = "";

    private VPlanSet mVPlanSet;

    /**
     * receives callbacks for specific actions
     */
    private final IVPlanLoader mListener;

    public Loader(Context context, IVPlanLoader listener) {
        mVPlanSet = new VPlanSet(context);
        mListener = listener;
    }

    public void getVPlan(boolean forceLoad) {
        new VPlanLoader(mVPlanSet).execute(forceLoad);
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
        if(loadCache) {
            getCachedVPlan();
        } else {
            mListener.vPlanLoaded(mVPlanSet);
        }
    }

    public interface IVPlanLoader {
        void vPlanLoaded(VPlanSet vplanset);
    }

    private class VPlanLoader extends AsyncTask<Boolean, Void, Void> {

        private static final String TAG = "VPlanLoader";

        private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
        private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

        /**
         * parsing constants
         */
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

        HttpClient mClient;
        VPlanSet mVPlanSet;
        boolean loadCache = false;

        public VPlanLoader(VPlanSet vPlanSet) {
            mClient = new DefaultHttpClient();
            mVPlanSet = vPlanSet;
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            boolean forceLoad = params[0];
            try {
                //Load the headers first to check if cache is current
                if (!forceLoad && mVPlanSet.readHeader()) {
                    String header1 = loadHeader(VPLAN1_URL);
                    String header2 = loadHeader(VPLAN2_URL);

                    if (mVPlanSet.getHeader1().equals(header1) && mVPlanSet.getHeader2().equals(header2)) {
                        loadCache = true;
                        return null;
                    }
                }
                HttpResponse res1 = loadPage(VPLAN1_URL);
                mVPlanSet.setHeader1(getHeader(res1));
                mVPlanSet.setVPlan1(parse(getVPlan(res1)));

                HttpResponse res2 = loadPage(VPLAN2_URL);
                mVPlanSet.setHeader2(getHeader(res2));
                mVPlanSet.setVPlan2(parse(getVPlan(res2)));

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
                header.put(HEADER_STATUS, status);
                header.put(HEADER_TITLE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());
            } catch (ArrayIndexOutOfBoundsException e) {
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
                loaderFinished(loadCache);
            }
        }
    }

}
