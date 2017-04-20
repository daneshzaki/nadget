package in.pleb.nadget;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * This class does the following:
 * checks the time the user wants to notify
 * displays notification
 */

public class Notifier {
    public static void process(Context mContext)
    {
        //TODO: get notifications display - yes/no from settings
        SharedPreferences prefs = mContext.getSharedPreferences("notifications", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        //if yes
        //get time of display
        //get current time
        //if current time = time of display then show notification
    /*
        if (System.currentTimeMillis() >= user_notification_time) {
            showNotification(mContext);
        }*/
    }


    public static void showNotification(final Context mContext)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.nadget2);
        mBuilder.setContentTitle("Nadget");
        mBuilder.setContentText("Time for some tech news!");

        //the activity to display
        Intent resultIntent = new Intent(mContext, NadgetMain.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(NadgetMain.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = 1;

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private final static String APP_TITLE = "Nadget";
    private final static int DAYS_UNTIL_PROMPT = 30;
    private final static int LAUNCHES_UNTIL_PROMPT = 30;


}
