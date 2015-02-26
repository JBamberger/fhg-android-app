package de.jbapps.vplan.util.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import de.jbapps.vplan.util.StorageManager;
import de.jbapps.vplan.util.database.VPlanDataSource;

public class VPlanUpdater extends AsyncTask<Boolean, Void, Boolean> {
    private static final String TAG = "VPlanLoader";

    private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    private IOnFinishedLoading mListener;
    VPlanDataSource mSource;
    StorageManager mStorageManager;
    private Context mContext;
    private HttpClient mClient;

    public VPlanUpdater(IOnFinishedLoading listener, Context context) {
        mSource = new VPlanDataSource(mContext);
        mStorageManager = new StorageManager(mContext);
        mClient = new DefaultHttpClient();
        mContext = context;
        this.mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        try {
            Log.i(TAG, "Loading VPlan");
            HttpResponse res = hGET(VPLAN1_URL);
            String vp = getVPlan(res);
            if (vp != null) {
                parse(vp, getHeader(res), true);
            } else {
                throw new ClientProtocolException("Empty response content");
            }

            res = hGET(VPLAN2_URL);
            vp = getVPlan(res);
            if (vp != null) {
                parse(vp, getHeader(res), false);
            } else {
                throw new ClientProtocolException("Empty response content");
            }
        } catch (ClientProtocolException | JSONException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private HttpResponse hGET(String url) throws IOException, JSONException {
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
                return header.getValue();
            }
        }
        return null;
    }

    private boolean parse(String vplan, String header, boolean dayOne) throws JSONException {
        mSource.open();
        Document doc = Jsoup.parse(vplan);

        //get date and day name
        //TODO: jPlan.put(DATE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());

        //get "Nachrichten zum Tag"
        Elements infoRows = doc.getElementsByClass("info").select("tr");
        StringBuilder motd = new StringBuilder();
        for (Element line : infoRows) {
            Elements cells = line.children().select("td");
            for (Element cell : cells) {
                motd.append(cell.text());
                motd.append(" ");
            }
            motd.append("\n");
        }
        mStorageManager.writeMOTD(motd.toString(), dayOne);

        //get vplan rows
        Elements listRows = doc.getElementsByClass("list").select("tr");
        for (Element line : listRows) {
            Elements cells = line.children();
            if (cells.select("th").size() == 0) {
                mSource.writeVItem(cells.get(0).text(),cells.get(3).text(),cells.get(2).text(),cells.get(4).text(),cells.get(1).text(),cells.get(5).text(), header);
            }
        }
        Log.i(TAG, "VPlan parsed and persisted");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (mListener != null) {
            mListener.onVPlanLoaded(success);
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanLoaded(boolean success);
    }
}
