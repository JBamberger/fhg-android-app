package de.jbapps.vplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import de.jbapps.vplan.util.StorageManager;

public class VPlanService extends Service {

    private static final String TAG = "VPlanService";

    private static final String VPLAN1_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm";
    private static final String VPLAN2_URL = "http://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm";

    private static final int NOTIFICATION_ID = 300;

    private static final int INIT_CLIENT = 0;
    private static final int RELOAD = 1;
    private static final int FORCE_RELOAD = 2;
    private static final int LOAD_HEADER_ONLY = 3;

    private Handler mTimer = new Handler();
    private HttpClient mClient;
    private StorageManager mStorage;

    //TODO: private Messenger mSensorService;
    //TODO: private final Messenger mServer = new Messenger(new IncomingHandler());
    //TODO: private ServiceConnection mSensorConnection = new SensorConnection();

    @Override
    public IBinder onBind(Intent intent) {
        return null;//TODO: mServer.getBinder(); //return the communication channel
    }

    private void sendMessage() {
        //TODO: implement
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Started.");
        mClient = new DefaultHttpClient();
        //mTimer.p
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /*void bindSensorService() {
        bindService(new Intent(this, SensorService.class), mSensorConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "SensorService bound");
    }

    private class SensorConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSensorService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, COM_REGISTER_CLIENT);
                msg.replyTo = mServer;
                mSensorService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "SensorService unreachable");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "SensorService crashed");
            mSensorService = null;
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }*/

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
        }

        private String getHeader(String url) throws IOException {
            HttpHead httpHead = new HttpHead(url);
            HttpResponse response = mClient.execute(httpHead);
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().contains("Last-Modified")) {
                    Log.i("VPlanLoader#getHeader()", header.getName() + " : " + header.getValue());
                    return header.getValue();
                }
            }
            return null;
        }
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
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
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
