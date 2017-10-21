package de.fhg_radolfzell.android_app.cloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.FHGApplication;
import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.util.Storage;
import de.fhg_radolfzell.android_app.main.MainActivity;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 04.08.2016.
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private static final long[] PATTERN = {500, 500, 400, 650};
    private static final int NOTIFICATION_DEFAULT = 0;
    private static final int NOTIFICATION_VPLAN_UPDATED = 1;
    private static final int NOTIFICATION_FEED_UPDATED = 2;
    private static final int NOTIFICATION_CALENDAR_UPDATED = 4;
    private static final String COLLAPSE_KEY_VPLAN_UPDATED = "plan_updated";


    @Inject
    Storage storage;


    @Override
    public void onCreate() {
        super.onCreate();
        ((FHGApplication) getApplication()).getAppComponent().inject(this);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage message object
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO: Handle messages

        Timber.d("Received FCM message from: %s", remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) { //data payload
            String key = remoteMessage.getCollapseKey();
            Map<String, String> data = remoteMessage.getData();
            Timber.d("Message data payload: %s", data);
            if (key == null) {
                // TODO: 05.08.2016 handle unique message
                sendNotification(NOTIFICATION_DEFAULT, "Unique message", data.toString());
                return;
            }
            switch (key) {
                case COLLAPSE_KEY_VPLAN_UPDATED:
                    if (data.containsKey("etag")) {
                        String etag = data.get("etag");
                        Timber.d("Message: %s Tag1: %s Tag2 %s", etag, storage.getLastSeenEtagVPlan1(), storage.getLastSeenEtagVPlan2());
                        //FIXME: maybe not working
                        if (!etag.equals(storage.getLastSeenEtagVPlan1()) && !etag.equals(storage.getLastSeenEtagVPlan2())) {
                            sendNotification(NOTIFICATION_VPLAN_UPDATED, getString(R.string.notification_title_vplan_updated), data.toString());//todo nice string

                        } else {
                            Timber.d("VPlan Update Message: etag already known %s", data.toString());
                        }
                    } else {
                        Timber.e("VPlan Update Message: etag is empty: %s", data.toString());
                    }
                    break;
            }
        }


        /*if (remoteMessage.getNotification() != null) { // Notification payload
//            only called if application is in the foreground
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Timber.d("Notification Message: title: %s\nbody: %s", notification.getTitle(), notification.getBody());
            sendNotification(NOTIFICATION_DEFAULT, notification.getTitle(), notification.getBody());
        }*/
    }

    /**
     * Copied from fcm example
     */
    private void sendNotification(int type, String title, String messageBody) {
        if (storage.getSettingsNotificationEnabled()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_vplan_update)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setTicker(messageBody).setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setLights(Color.GREEN, 1000, 1000)
                    .setContentIntent(pendingIntent);
            if (storage.getSettingsNotificationVibrate()) {
                notificationBuilder.setVibrate(PATTERN);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(type, notificationBuilder.build());

        }
    }
}