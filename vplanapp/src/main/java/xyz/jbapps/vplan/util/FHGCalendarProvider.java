package xyz.jbapps.vplan.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.data.VPlanRow;

public class FHGCalendarProvider extends AsyncTask<Object, Object, Boolean> {

    /**
     * TAG used for debug logging
     */
    private static final String TAG = "FHGCalendarProvider";
    /**
     * indicates operation success
     */
    private static final boolean SUCCESS = true;

    /**
     * indicates operation failure
     */
    private static final boolean FAILURE = false;

    /**
     * url of first vplan fragment
     */
    private static final String URL_VPLAN1 = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";

    /**
     * http header last modified
     */
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    /**
     * used to parse http header dates
     */
    private final SimpleDateFormat dateParser;

    /**
     * notified as soon as the operation terminates
     */
    private final IFHGCalendarResultListener listener;

    /**
     * type of operation
     */
    private final int type;

    /**
     * @param context  used to access cache
     * @param listener receives result data
     * @param type     determines loading algorithm, use constants
     */
    public FHGCalendarProvider(Context context, int type, IFHGCalendarResultListener listener) {
        this.dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        this.listener = listener;
        this.type = type;
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        switch (type) {
            default:
                return FAILURE;
        }
    }

    @Override
    protected void onPostExecute(Boolean o) {
        if (listener != null) {
            if (o) {
                listener.fhgCalendarLoadingSucceeded(null);
            } else {
                listener.fhgCalendarLoadingFailed();
            }
        }
    }

    /**
     * This method loads the given url and returns the given document as {@link String}
     *
     * @param url url to load
     * @return document as string
     * @throws IOException
     */
    @NonNull
    private FHGCalendarObject loadCalendarFromNet(String url) throws IOException {
        URL vplan_url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) vplan_url.openConnection();
        try {
            connection.setRequestMethod("GET");
            Map<String, List<String>> headers = connection.getHeaderFields();
            long header = getLastModified(headers);
            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
                return new FHGCalendarObject(out.toString(), header);

            } else {
                throw new IOException("Networking error");
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * This method loads the given url and returns the given document as {@link String}
     *
     * @param url url to load
     * @return last_modified header as long
     * @throws IOException
     */
    private long loadFHGCalendarHeaderFromNet(String url) throws IOException {
        URL vplan_url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) vplan_url.openConnection();
        try {
            connection.setRequestMethod("HEAD");
            Map<String, List<String>> headers = connection.getHeaderFields();

            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                return getLastModified(headers);
            } else {
                throw new IOException("http response not ok; response code is: " + resCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * extracts the last modified header out of the map
     *
     * @param headers header map
     * @return header converted to long
     * @throws IOException if header is not found
     */
    private long getLastModified(Map<String, List<String>> headers) throws IOException {
        if (headers.containsKey(HEADER_LAST_MODIFIED)) {
            String lastModified = headers.get(HEADER_LAST_MODIFIED).get(0);
            //Wed, 05 Aug 2015 10:21:47 GMT
            try {
                return dateParser.parse(lastModified).getTime();
            } catch (ParseException e) {
                throw new IOException("could not parse header date");
            }
        } else {
            throw new IOException("header " + HEADER_LAST_MODIFIED + " not in given data set");
        }
    }


    @Nullable
    private VPlanRow readVPlanTableCells(Elements cells) {
        VPlanRow row = new VPlanRow();
        if (cells.select("th").size() == 0 && cells.size() >= 6) {
            row.setSubject(cells.get(3).text());
            row.setHour(cells.get(1).text());
            row.setGrade(cells.get(0).text());
            row.setContent(cells.get(2).text());
            row.setRoom(cells.get(4).text());
            row.setOmitted(cells.get(5).text().contains("x"));
            row.setMarkedNew(cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
            return row;
        } else {
            Log.i(TAG, "Given data contains table head");
            return null;
        }
    }

    public interface IFHGCalendarResultListener {
        /**
         * If the loading process fails in some case the listener will be notified so the UI can be
         * updated accordingly.
         */
        void fhgCalendarLoadingFailed();

        /**
         * If the loading process succeeded the listener will receive the vplan data
         */
        void fhgCalendarLoadingSucceeded(List<String> calendar);
    }

    /**
     * Temporary class to pass header and vplan as return value
     */
    private class FHGCalendarObject {
        final String vplan;
        final long header;

        public FHGCalendarObject(String vplan, long header) {
            this.vplan = vplan;
            this.header = header;
        }
    }
}
