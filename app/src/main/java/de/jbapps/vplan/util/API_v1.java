package de.jbapps.vplan.util;

import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the API in version 1.0
 * It contains the corresponding methods to 'add', 'ping' and 'trigger'
 */
public class API_v1 {

    private static final String TAG = "API_v1";

    private static final String API_ADD = "http://fhg42-vplanapp.rhcloud.com/add";
    private static final String API_PING = "http://fhg42-vplanapp.rhcloud.com/ping";
    private static final String API_TRIGGER = "http://fhg42-vplanapp.rhcloud.com/trigger";

    public void doTrigger() {
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


    }

    public void doPing(final String id) {
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

    public void doAdd(final String gcmId, final Property property) {
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

    private HttpURLConnection getDefaultURLConnection(String URL) throws IOException {
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