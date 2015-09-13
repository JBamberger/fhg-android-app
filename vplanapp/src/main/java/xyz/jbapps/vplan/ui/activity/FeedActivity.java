package xyz.jbapps.vplan.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;

public class FeedActivity extends AppCompatActivity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String FEED = "http://fhg-radolfzell.de/feed/atom/";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPage();
    }

    private void loadPage() {
        new DownloadXmlTask().execute(FEED);
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {e.printStackTrace();
                return "ioi ex";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return "parse ex";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            setContentView(R.layout.main);
            WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.loadData(result, "text/html", null);
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        FHGFeedXmlParser FHGFeedXmlParser = new FHGFeedXmlParser();
        List<FHGFeedXmlParser.FHGFeedItem> entries = null;
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");


        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>" + "" + "</h3>");
        htmlString.append("<em>" + "" + " " +
                formatter.format(rightNow.getTime()) + "</em>");

        try {
            stream = downloadUrl(urlString);
            entries = FHGFeedXmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        for (FHGFeedXmlParser.FHGFeedItem entry : entries) {
            htmlString.append("<p><a href='");
            htmlString.append(entry.link);
            htmlString.append("'>" + entry.title + "</a></p>");
        }
        return htmlString.toString();
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
