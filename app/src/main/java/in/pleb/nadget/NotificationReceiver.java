package in.pleb.nadget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by danesh on 23-04-2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "NotificationReceiver showNotification");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat_ng);
        mBuilder.setContentTitle("Time for some tech news!");
        mBuilder.setContentText("Touch to refresh");
        mBuilder.setColor(Color.parseColor("#3B3131"));
        mBuilder.setAutoCancel(true);


        //the activity to display
        Intent resultIntent = new Intent(context, NadgetMain.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NadgetMain.class);

        Log.i(TAG, "NotificationReceiver showNotification 2");

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = 1;

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
        Log.i(TAG, "NotificationReceiver showNotification 3");

    }

    private static final String TAG = "Nadget";
}

