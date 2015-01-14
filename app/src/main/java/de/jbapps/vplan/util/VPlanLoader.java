package de.jbapps.vplan.util;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class VPlanLoader extends AsyncTask<Void, Void, Void> {

    IOnFinishedLoading mListener;
    String vplan1;
    String vplan2;

    public VPlanLoader(IOnFinishedLoading listener) {
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            vplan1 = load("http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm");
            vplan2 = load("http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String load(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        return httpclient.execute(httpGet, new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        });
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mListener != null) {
            if (vplan1 != null || vplan2 != null) {
                mListener.onVPlanLoaded(vplan1, vplan2);
            } else {
                mListener.onVPlanLoadingFailed();
            }
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanLoaded(String v1, String v2);

        public void onVPlanLoadingFailed();
    }
}