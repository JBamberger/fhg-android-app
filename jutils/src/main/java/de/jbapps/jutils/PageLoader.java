package de.jbapps.jutils;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PageLoader extends AsyncTask<String, Void, Void> {

    private IPageLoader mListener;
    private URL mURL;

    public PageLoader(IPageLoader listener, String url) throws IllegalArgumentException {
        if (listener != null) {
            this.mListener = listener;
        } else {
            throw new IllegalArgumentException("listener must not be null");
        }
        try {
            this.mURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url is invalid");
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {


        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) mURL.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            /*connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(NetUtils.getQuery(nameValuePairs));
            writer.flush();
            writer.close();
            os.close();*/

            connection.connect();
            InputStream is = connection.getInputStream();
            Reader in = new InputStreamReader(is, connection.getContentEncoding());
            try {
                //TODO: connection.getResponseCode();
                final int bufferSize = 1024;
                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();
                for (; ; ) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
                String content = out.toString();

            } finally {
                is.close();
                in.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.onLoadingProgressUpdated();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public interface IPageLoader {

        void onLoadingProgressUpdated();

        void onLoadingComplete(String content, Map<String, String> headers);

    }
}
