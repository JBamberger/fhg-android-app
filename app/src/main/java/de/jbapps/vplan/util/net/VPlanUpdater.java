package de.jbapps.vplan.util.net;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
        if (params[0]) {
            String[] oldHeader = mStorageManager.readHeaders();
            String[] newHeader = new String[2];
            try {
                newHeader[0] = getHeader(executeHEAD(VPLAN1_URL));
                newHeader[1] = getHeader(executeHEAD(VPLAN2_URL));
                if (!oldHeader[0].equals(newHeader[0]) || !oldHeader[1].equals(newHeader[1]))
                    return updateFullVPlan();
                else
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return updateFullVPlan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean updateFullVPlan() throws IOException {
        return loadVPlan(true) && loadVPlan(false);
    }

    private boolean loadVPlan(boolean first) {
        String url;
        if (first)
            url = VPLAN1_URL;
        else
            url = VPLAN2_URL;
        try {
            HttpResponse response = executeGET(url);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                String header = getHeader(response);

                String vp = EntityUtils.toString(entity);
                parse(vp, header, first);
                return true;
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HttpResponse executeGET(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return mClient.execute(httpGet);
    }

    private Header[] executeHEAD(String url) throws IOException {
        HttpHead httpHead = new HttpHead(url);
        return mClient.execute(httpHead).getAllHeaders();
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

    private String getHeader(Header[] headers) {
        for (Header header : headers) {
            if (header.getName().contains("Last-Modified")) {
                return header.getValue();
            }
        }
        return null;
    }

    private void parse(String vplan, String header, boolean first) {
        mSource.open();
        Document doc = Jsoup.parse(vplan);

        mStorageManager.writeVPlanTitle(doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text(), first);

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
        mStorageManager.writeMOTD(motd.toString(), first);

        Elements listRows = doc.getElementsByClass("list").select("tr");
        for (Element line : listRows) {
            Elements cells = line.children();
            if (cells.select("th").size() == 0) {
                mSource.writeVItem(cells.get(0).text(), cells.get(3).text(), cells.get(2).text(), cells.get(4).text(), cells.get(1).text(), cells.get(5).text(), header);
            }
        }
        mStorageManager.writeHeader(header, first);
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
