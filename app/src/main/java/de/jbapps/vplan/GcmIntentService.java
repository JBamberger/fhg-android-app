package de.jbapps.vplan;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.jbapps.vplan.data.VPlanSet;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID_VPLAN = 1;
    public static final int NOTIFICATION_ID_APP_UPDATE = 2;
    private static final String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.i(TAG, "GCM received: " + extras.toString());

                String hint = extras.getString("hint");
                Log.i(TAG, "hint: " + hint);
                if (hint.equals("possible_update")) {
                    String version = extras.getString("version");
                    VPlanSet set = new VPlanSet(this);
                    set.readHeader();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

                    try {
                        long time1 = sdf.parse(set.getHeader1()).getTime();
                        Log.i(TAG, "header1: " + set.getHeader1() + " time: " + time1);
                        long time2 = sdf.parse(set.getHeader2()).getTime();
                        Log.i(TAG, "header2: " + set.getHeader2() + " time: " + time2);
                        long versionTime = sdf.parse(version).getTime();
                        Log.i(TAG, "rHeader: " + version + " time: " + versionTime);

                        if (versionTime > time1 || versionTime > time2) {
                            sendNotification(NOTIFICATION_ID_VPLAN, "VPlan aktualisiert", "Received: " + extras.toString());
                            Log.i(TAG, "Notification published.");
                        } else {
                            Log.i(TAG, "Notification aborted, received header not current");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(int Id, String title, String msg) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (pref.getBoolean("notification_enabled", true)) {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg)
                    .setTicker(msg)
                    .setSound(Uri.parse(pref.getString("notification_ringtone", "DEFAULT_SOUND")));


            //if (Id == NOTIFICATION_ID_VPLAN) {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, VPlanActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*} else if (Id == NOTIFICATION_ID_APP_UPDATE) {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.de/")), 0); //TODO: VPlan update page
                mBuilder.setContentIntent(contentIntent);
            }*/

            if (pref.getBoolean("notification_vibrate", true)) {
                long[] pattern = {500, 500, 400, 650};
                mBuilder.setVibrate(pattern);
            }

            String color = pref.getString("notification_color", "#00ff00");
            if ("#FF0000".equals(color)) {
                mBuilder.setLights(Color.RED, 1000, 1000);
            } else if ("#00FF00".equals(color)) {
                mBuilder.setLights(Color.GREEN, 1000, 1000);
            } else if ("#0000FF".equals(color)) {
                mBuilder.setLights(Color.BLUE, 1000, 1000);
            }
            mNotificationManager.notify(Id, mBuilder.build());
        }
    }
}
