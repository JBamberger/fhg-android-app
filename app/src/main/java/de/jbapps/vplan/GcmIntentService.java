package de.jbapps.vplan;

public class GcmIntentService {/*extends IntentService {
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

                sendNotification(NOTIFICATION_ID_VPLAN, "VPlan aktualisiert", "Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(int Id, String title, String msg) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (pref.getBoolean("notification_enabled", true)) {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg)
                    .setTicker(msg)
                    .setSound(Uri.parse(pref.getString("notification_ringtone", "DEFAULT_SOUND")));


            if (Id == NOTIFICATION_ID_VPLAN) {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, VPlanActivity.class), 0);
                mBuilder.setContentIntent(contentIntent);
            } else if (Id == NOTIFICATION_ID_APP_UPDATE) {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.de/")), 0); //TODO: VPlan update page
                mBuilder.setContentIntent(contentIntent);
            }

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
    }*/
}
