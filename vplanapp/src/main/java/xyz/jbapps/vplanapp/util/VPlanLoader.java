package xyz.jbapps.vplanapp.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import xyz.jbapps.vplanapp.VPlanException;

public class VPlanLoader implements Runnable {
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

    private static final String HEADER_LAST_MODIFIED = "Last-Modified";

    private boolean loadCache = false;

    private SimpleDateFormat mDateParser;

    public VPlanLoader() {
        mDateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    }

    @Override
    public void run() {
        System.out.println(loadVPlan(URL_VPLAN1));
        System.out.println(loadVPlan(URL_VPLAN2));
    }

    private String loadVPlan(String url) {
        try {
            URL vplan_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) vplan_url.openConnection();
            try {
                connection.setRequestMethod("GET");
                Map<String, List<String>> headers = connection.getHeaderFields();
                //System.out.println(connection.getDate());
                //System.out.println(getHeaderValue(headers, HEADER_LAST_MODIFIED));
                int resCode = connection.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                    }
                    System.out.println(out.toString());
                    reader.close();
                    return parseVPlan(out.toString()).toString();

                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private long loadVPlanHeader(String url) throws VPlanException {
        try {
            URL vplan_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) vplan_url.openConnection();
            try {
                connection.setRequestMethod("HEAD");
                Map<String, List<String>> headers = connection.getHeaderFields();

                int resCode = connection.getResponseCode();
                if (resCode >= 200 && resCode < 300) {
                    return getHeaderValue(headers, HEADER_LAST_MODIFIED);
                } else {
                    throw new VPlanException("http response not ok; response code is: " + resCode);
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new VPlanException("failed to connect to given server");
        }
    }

    private long getHeaderValue(Map<String, List<String>> headers, String headerName) throws VPlanException {
        if (headers.containsKey(headerName)) {
            String lastModified = headers.get(headerName).get(0);
            //Wed, 05 Aug 2015 10:21:47 GMT
            try {
                return mDateParser.parse(lastModified).getTime();
            } catch (ParseException e) {
                throw new VPlanException("could not parse header date");
            }
        } else {
            throw new VPlanException("header " + headerName + " not in given data set");
        }
    }

    /*

        private void listKeys(Map<String, List<String>> headers) {
            for (String key : headers.keySet()) {
                System.out.println(key);
            }
        }

            @Override
            protected Void doInBackground(Boolean... params) {
                Log.e(TAG, "doInBackground invoked");
                boolean forceLoad = params[0];
                Log.i(TAG, "force loading on:" + forceLoad);
                try {
                    //Load the headers first to check if cache is up to date
                    if (!forceLoad && mVPlanSet.readHeader()) {
                        String header1 = loadHeader(URL_VPLAN1);
                        String header2 = loadHeader(URL_VPLAN2);

                        if (mVPlanSet.getHeader1().equals(header1) && mVPlanSet.getHeader2().equals(header2)) {
                            loadCache = true;
                            Log.d(TAG, "loadCache: true");
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
        */
    private JSONObject parseVPlan(String vplan) throws JSONException {
        JSONObject jPlan = new JSONObject();
        Document doc = Jsoup.parse(vplan);
        jPlan.put(JSON_HEADER, readHeaderFromDocument(vplan, doc));
        jPlan.put(JSON_MOTD, readMotdTable(doc.getElementsByClass("info").select("tr")));
        jPlan.put(JSON_VPLAN, readVPlanTable(doc.getElementsByClass("list").select("tr")));
        Log.i(TAG, "VPlan parsed");
        return jPlan;
    }

    private JSONObject readHeaderFromDocument(String vplan, Document vplanDoc) throws JSONException {
        JSONObject header = new JSONObject();
        try {
            String status = vplan.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            header.put(JSON_HEADER_STATUS, status);
            header.put(JSON_HEADER_TITLE, vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            header.put(JSON_HEADER_STATUS, "The VPlan-file is corrupted.");
            header.put(JSON_HEADER_TITLE, "~ please contact the support");
        }
        return header;
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

    @NonNull
    private JSONArray readVPlanTable(Elements vPlanTable) throws JSONException {
        JSONArray vPlan = new JSONArray();
        for (Element line : vPlanTable) {
            Elements cells = line.children();
            JSONObject row = readVPlanTableCells(cells);
            if (row != null) {
                vPlan.put(row);
            }
        }
        return vPlan;
    }

    @Nullable
    private JSONObject readVPlanTableCells(Elements cells) throws JSONException {
        if (cells.select("th").size() == 0 && cells.size() >= 6) {
            JSONObject row = new JSONObject();
            row.put(JSON_VPLAN_SUBJECT, cells.get(3).text());
            row.put(JSON_VPLAN_HOUR, cells.get(1).text());
            row.put(JSON_VPLAN_GRADE, cells.get(0).text());
            row.put(JSON_VPLAN_CONTENT, cells.get(2).text());
            row.put(JSON_VPLAN_ROOM, cells.get(4).text());
            row.put(JSON_VPLAN_OMITTED, cells.get(5).text().contains("x"));
            row.put(JSON_VPLAN_MARKED_NEW, cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
            return row;
        } else {
            Log.i(TAG, "Given data contains table head");
            return null;
        }
    }

/*
    @Override
    protected void onPostExecute(Void v) {
        Log.e(TAG, "onPostExecuted invoked");
        if (mListener != null) {
            mListener.loaderFinished(loadCache);
        }
    }

    public interface IOnLoadingFinished {
        void loaderFinished(boolean loadCache);
    }*/
}
