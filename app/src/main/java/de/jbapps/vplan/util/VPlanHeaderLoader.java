package de.jbapps.vplan.util;

import android.os.AsyncTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class VPlanHeaderLoader  extends AsyncTask<Void, Void, Void> {

    IOnFinishedLoading mListener;
    String vplan1;
    String vplan2;

    public VPlanHeaderLoader(/*IOnFinishedLoading listener*/) {
        //this.mListener = listener;
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
        HttpHead httpHead = new HttpHead(url);
        HttpResponse response =  httpclient.execute(httpHead);

        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            System.out.println("Key : " + header.getName()
                    + " ,Value : " + header.getValue());

        }
        System.out.println("############################################");

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mListener != null) {
            if (vplan1 != null || vplan2 != null) {
                mListener.onVPlanHeaderLoaded(vplan1, vplan2);
            } else {
                mListener.onVPlanHeaderLoadingFailed();
            }
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanHeaderLoaded(String v1, String v2);

        public void onVPlanHeaderLoadingFailed();
    }
}