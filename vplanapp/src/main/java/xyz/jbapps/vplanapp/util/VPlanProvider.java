package xyz.jbapps.vplanapp.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

import xyz.jbapps.vplanapp.data.VPlanData;
import xyz.jbapps.vplanapp.data.VPlanRow;

/**
 * This class is used to load vplan html parse it and cache it. Results are published using the
 * {@link xyz.jbapps.vplanapp.util.VPlanProvider.IVPlanResultListener}.
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
public class VPlanProvider extends AsyncTask<Object, Object, Boolean> {


    /**
     * load the VPlan cache without checking the network resources
     */
    public static final int TYPE_LOAD = 0;
    /**
     * load the VPlan. The local cache is checked first against the VPlan headers to
     * verify the cache status. Then the cache is loaded if current or the network resources if not.
     * In case the cache is outdated it will be updated. Using this type avoids unnecessary network usage.
     */
    public static final int TYPE_CACHE = 1;
    /**
     * load the network resources ignoring the local cache. The cache is updated afterwards.
     */
    public static final int TYPE_FORCE_LOAD = 2;
    /**
     * TAG used for debug logging
     */
    private static final String TAG = "VPlanProvider";
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
     * url of second vplan fragment
     */
    private static final String URL_VPLAN2 = "http://intranet.sihg.edu-singen.de/untis_public/f1/subst_001.htm";//http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

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
    private final IVPlanResultListener listener;

    /**
     * provides access to vplan cache
     */
    private final VPlanCache vPlanCache;

    /**
     * type of operation
     */
    private final int type;

    /**
     * the desired output data of vplan 1
     */
    private VPlanData vPlanData1;

    /**
     * the desired output data of vplan 2
     */
    private VPlanData vPlanData2;

    /**
     * @param context  used to access cache
     * @param listener receives result data
     * @param type     determines loading algorithm, use constants
     */
    public VPlanProvider(Context context, int type, IVPlanResultListener listener) {
        this.dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        this.listener = listener;
        this.type = type;
        this.vPlanCache = new VPlanCache(context);
    }

    /**
     * loads cache without checking network for updates
     *
     * @return successful
     */
    private boolean typeCache() {
        try {
            vPlanData1 = vPlanCache.readVPlan(VPlanCache.FILE_VPLAN1);
            vPlanData2 = vPlanCache.readVPlan(VPlanCache.FILE_VPLAN2);
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return FAILURE;
        }
    }

    /**
     * loads network data without checking cache
     *
     * @return successful
     */
    private boolean typeForceLoad() {
        try {
            vPlanData1 = parseVPlan(loadVPlanFromNet(URL_VPLAN1));
            vPlanData2 = parseVPlan(loadVPlanFromNet(URL_VPLAN2));
            vPlanCache.writeVPlan(vPlanData1, VPlanCache.FILE_VPLAN1);
            vPlanCache.writeVPlan(vPlanData2, VPlanCache.FILE_VPLAN2);
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return FAILURE;
        }
    }


    /**
     * checks if local cache is current and takes appropriate steps if not
     *
     * @return successful
     */
    private boolean typeLoad() {
        try {
            VPlanData vdata1 = vPlanCache.readVPlan(VPlanCache.FILE_VPLAN1);
            VPlanData vdata2 = vPlanCache.readVPlan(VPlanCache.FILE_VPLAN2);
            long h1 = loadVPlanHeaderFromNet(URL_VPLAN1);
            long h2 = loadVPlanHeaderFromNet(URL_VPLAN2);
            long h1_cache = vdata1.getLastUpdated();
            long h2_cache = vdata2.getLastUpdated();
            if ((h1 == h1_cache) && (h2 == h2_cache)) {
                vPlanData1 = vdata1;
                vPlanData2 = vdata2;
                return SUCCESS;
            } else {
                return typeForceLoad();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return typeForceLoad();
        }
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        switch (type) {
            case TYPE_CACHE:
                return typeCache();
            case TYPE_LOAD:
                return typeLoad();
            case TYPE_FORCE_LOAD:
                return typeForceLoad();
            default:
                return FAILURE;
        }
    }

    @Override
    protected void onPostExecute(Boolean o) {
        if (listener != null) {
            if (o) {
                listener.vPlanLoadingSucceeded(vPlanData1, vPlanData2);
            } else {
                listener.vPlanLoadingFailed();
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
    private VPlanObject loadVPlanFromNet(String url) throws IOException {
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
                return new VPlanObject(out.toString(), header);

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
    private long loadVPlanHeaderFromNet(String url) throws IOException {
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

    /**
     * @param vplan VPlanObject to parse
     * @return VPlanData
     * @throws IOException if an error occurs while parsing
     */
    @NonNull
    private VPlanData parseVPlan(VPlanObject vplan) throws IOException {
        try {
            String vplanString = vplan.vplan;
            long header = vplan.header;
            VPlanData vData = new VPlanData();
            vData.setLastUpdated(header);
            Document doc = Jsoup.parse(vplanString);
            vData.setStatus(readVPlanStatus(vplanString));
            vData.setTitle(readVPlanTitle(doc));
            vData.setMotd(readMotdTable(doc.getElementsByClass("info").select("tr")));
            readVPlanTable(doc.getElementsByClass("list").select("tr"), vData);
            Log.i(TAG, "VPlan parsed");
            return vData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("parsing of vplan failed");
        }
    }

    @NonNull
    private String readVPlanStatus(String vplan) {
        try {
            String status = vplan.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            return status;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "VPlan file is corrupted, an error occurred...";
        }
    }

    @NonNull
    private String readVPlanTitle(Document vplanDoc) {
        return vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text();

    }

    @NonNull
    private String readMotdTable(Elements motdTable) {
        StringBuilder motd = new StringBuilder();
        for (Element line : motdTable) {
            Elements cells = line.children().select("td");
            for (Element cell : cells) {
                motd.append(cell.text());
                motd.append("\n");
            }
        }
        //TODO: remove last newline character
        return motd.toString();
    }

    private void readVPlanTable(Elements vPlanTable, VPlanData vData) {
        for (Element line : vPlanTable) {
            Elements cells = line.children();
            VPlanRow row = readVPlanTableCells(cells);
            if (row != null) {
                vData.addVPlanRow(row);
            }
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
            row.setMarkedNew(cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]40"));
            return row;
        } else {
            Log.i(TAG, "Given data contains table head");
            return null;
        }
    }

    public interface IVPlanResultListener {
        /**
         * If the loading process fails in some case the listener will be notified so the UI can be
         * updated accordingly.
         */
        void vPlanLoadingFailed();

        /**
         * If the loading process succeeded the listener will receive the vplan data
         */
        void vPlanLoadingSucceeded(VPlanData vplan1, VPlanData vplan2);
    }

    /**
     * Temporary class to pass header and vplan as return value
     */
    private class VPlanObject {
        final String vplan;
        final long header;

        public VPlanObject(String vplan, long header) {
            this.vplan = vplan;
            this.header = header;
        }
    }
}
