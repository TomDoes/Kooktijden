package com.tomdoesburg.kooktijden;

/**
 * Created by FrankD on 9-8-2015.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//receives alarm intents and shows a notification
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private final String INTENT_ACTION = "INTENT_ACTION";
    public static final String NOTIFICATION_INFO = "NOTIFICATION_INFO"; //used in AlarmReceiver class

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"Received intent");

        if(intent.getAction().equals(INTENT_ACTION)) {
            Intent newIntent = new Intent(context, MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

            String notificationText = "";

            try {
                notificationText = intent.getExtras().getString(NOTIFICATION_INFO);

            } catch (NullPointerException ex) {
                Log.v(TAG, "could not produce notification text");
                notificationText = context.getString(R.string.timer_ready);
            }
            Log.v(TAG, "notification text is " + notificationText);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(context.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notificationText))//expanded text
                            .setContentText(notificationText)  //small text
                            .setContentIntent(pIntent)
                            .setDefaults(NotificationCompat.DEFAULT_ALL) //vibrate, sound and blinking light
                            .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mBuilder.build());

        }
    }

    //set transparent logo for android lollipop
    private int getNotificationIcon() {
        return R.drawable.kooktijden_icon;
    }
}