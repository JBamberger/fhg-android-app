package de.jbapps.vplan.util;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class represents the API in version 1.0
 * It contains the corresponding methods to 'add', 'ping' and 'trigger'
 */
public class API_v1 {

    private static final String TAG = "API_v1";

    private static final String API_ADD = "http://fhg42-vplanapp.rhcloud.com/add";
    private static final String API_PING = "http://fhg42-vplanapp.rhcloud.com/ping";
    private static final String API_TRIGGER = "http://fhg42-vplanapp.rhcloud.com/trigger";
    private static final String API_TRIGGER_DEBUG = "http://fhg42-vplanapp.rhcloud.com/trigger/1";

    /*public static void doTrigger() {
        Log.i(TAG, "Invoking trigger");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = getDefaultURLConnection(API_TRIGGER);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    String content = IOUtils.toString(in, "UTF-8");
                    Log.i(TAG, "Trigger Response: " + content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
                return null;
            }
        }.execute();
    }*/

    public static void doTrigger(final String header1, final String header2) {
        Log.i(TAG, "Invoking trigger");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = getDefaultURLConnection(API_TRIGGER);

                    String header = getRecentHeader(header1, header2);
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("updated_at", header));
                    Log.i(TAG, "Sending invoke trigger with \"" + header + "\"");
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(NetUtils.getQuery(nameValuePairs));
                    writer.flush();
                    writer.close();
                    os.close();

                    connection.connect();
                    InputStream in = connection.getInputStream();
                    String content = IOUtils.toString(in, "UTF-8");
                    Log.i(TAG, "Trigger Response: " + content);
                    handleTrigger(content);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
                return null;
            }
        }.execute();


    }

    private static String getRecentHeader(String h1, String h2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        Date d1 = format.parse(h1);
        Date d2 = format.parse(h2);
        if (d1.getTime() > d2.getTime()) {
            return h1;
        } else {
            return h2;
        }
    }

    private static void handleTrigger(String response) throws JSONException {
        if (response == null) return;
        JSONObject res = new JSONObject(response);
        String status = res.getString("status");
        String output64 = res.getString("output64");
        Log.i(TAG, "Status: " + status);
        Log.i(TAG, new String(Base64.decode(output64, Base64.DEFAULT)));
        //TODO: evaluate data
    }

    public static void doPing(final String id) {
        Log.i(TAG, "Pinging with id: " + id);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = getDefaultURLConnection(API_PING);

                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("id", id));

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(NetUtils.getQuery(nameValuePairs));
                    writer.flush();
                    writer.close();
                    os.close();
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    String content = IOUtils.toString(in, "UTF-8");
                    Log.i(TAG, "Ping Response: " + content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
                return null;
            }
        }.execute();
    }

    public static void doAdd(final String gcmId, final Property property) {
        Log.i(TAG, "Adding gcmId: " + gcmId);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = getDefaultURLConnection(API_ADD);

                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("gcm", gcmId));

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(NetUtils.getQuery(nameValuePairs));
                    writer.flush();
                    writer.close();
                    os.close();
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    String content = IOUtils.toString(in, "UTF-8");
                    Log.i(TAG, "Add Response: " + content);
                    JSONObject json = new JSONObject(content);
                    switch (Integer.parseInt(json.getString("status"))) {
                        case 0:
                            Log.e(TAG, "Adding failed: " + json.getString("error"));
                            break;
                        case 1:
                            String vId = json.getString("insert");
                            property.storeClientId(vId);
                            break;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
                return null;
            }
        }.execute();
    }

    private static HttpURLConnection getDefaultURLConnection(String URL) throws IOException {
        HttpURLConnection connection;
        java.net.URL address = new URL(URL);
        connection = (HttpURLConnection) address.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
}