package de.jbapps.vplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import de.jbapps.vplan.ui.activity.MainActivity;
import de.jbapps.vplan.util.StorageManager;
import de.jbapps.vplan.util.net.VPlanUpdater;

public class VPlanService extends Service implements VPlanUpdater.IOnFinishedLoading {

    private static final String TAG = "VPlanService";

    private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    private static final int NOTIFICATION_ID = 300;
    public static final int INIT_CLIENT = 0;
    public static final int RELOAD = 1;
    public static final int FORCE_RELOAD = 2;
    public static final int CHECK_HEADER = 3;

    public static final int LOADING_FINISHED = 100;

    private static final long DELAY_TIME = 5 * 60 * 1000;

    private Handler mTimer = new Handler();
    private HttpClient mClient;
    private Context mContext;
    private VPlanUpdater mUpdater;
    private StorageManager mStorage;
    private Messenger mActivity;
    private final Messenger mServer = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mServer.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Started.");
        mClient = new DefaultHttpClient();
        mStorage = new StorageManager(this);
        mTimer.postDelayed(new CheckVPlan(), DELAY_TIME);
        mContext = this;
    }

    private void reloadVplan() {
        mUpdater = new VPlanUpdater(this, this);
        mUpdater.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CLIENT:
                    mActivity = msg.replyTo;
                    break;
                case RELOAD:
                    reloadVplan();
                    break;
                case CHECK_HEADER:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class CheckVPlan implements Runnable {
        @Override
        public void run() {

            try {
                String[] newHeaders = new String[2];
                newHeaders[0] = getHeader(VPLAN1_URL);
                newHeaders[1] = getHeader(VPLAN2_URL);
                String[] oldHeaders = mStorage.readHeaders();
                if (!newHeaders[0].equals(oldHeaders[0]) || !newHeaders[1].equals(oldHeaders[1])) {
                    showNotification();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Can't load headers");
            }
            if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("checkbox_update", true))
                mTimer.postDelayed(new CheckVPlan(), DELAY_TIME);
        }

        private String getHeader(String url) throws IOException {
            HttpHead httpHead = new HttpHead(url);
            HttpResponse response = mClient.execute(httpHead);
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().contains("Last-Modified")) {
                    Log.i(TAG, header.getName() + " : " + header.getValue());
                    return header.getValue();
                }
            }
            return null;
        }
    }

    private void showNotification() {
        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("VPlan aktualisiert")
                        .setContentText("Der Vertretungsplan wurde aktualisiert.");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            resultPendingIntent = PendingIntent.getBroadcast(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (pref.getBoolean("checkbox_notification", true)) {

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("VPlan aktualisiert")
                    .setContentText("Der Vertretungsplan wurde aktualisiert.")
                    .setTicker("Der Vertretungsplan wurde aktualisiert.")
                    .setSound(Uri.parse(pref.getString("notification_ringtone", "DEFAULT_SOUND")))
                    .setContentIntent(contentIntent);

            if (pref.getBoolean("checkbox_vibrate", true)) {
                long[] pattern = {500, 500, 400, 650};
                mBuilder.setVibrate(pattern);
            }
            String color = pref.getString("notification_color", "#00ff00");
            switch (color) {
                case "#FF0000":
                    mBuilder.setLights(Color.RED, 1000, 1000);
                    break;
                case "#00FF00":
                    mBuilder.setLights(Color.GREEN, 1000, 1000);
                    break;
                case "#0000FF":
                    mBuilder.setLights(Color.BLUE, 1000, 1000);
                    break;
            }

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                resultPendingIntent = PendingIntent.getBroadcast(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    @Override
    public void onVPlanLoaded(boolean success) {
        //TODO: implement
        Message msg = new Message();
        msg.what = LOADING_FINISHED;
        try {
            mActivity.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
