package in.pleb.nadget;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import org.ocpsoft.prettytime.*;

import java.util.Calendar;

/**
 * This class does the following:
 * gets the time the user wants to notify from prefs
 * displays notification using alarm manager
 */

public class Notifier {
    public static void process(Context context)
    {

        userPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //set up alarm manager and intents
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //if notifications are enabled
        if (userPreferences.getBoolean("notify", false)) {

            //get notification time
            int notifyTimeHr = userPreferences.getInt("notifyTimeHr", 0);
            int notifyTimeMin = userPreferences.getInt("notifyTimeMin", 0);

            Log.i(TAG, "Notifier notifyTimeHr = "+notifyTimeHr);
            Log.i(TAG, "Notifier notifyTimeMin = "+notifyTimeMin);

            //set the alarm to start at the said time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, notifyTimeHr);
            calendar.set(Calendar.MINUTE, notifyTimeMin);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
        else {
            Log.i(TAG, "Notifier notification cancelled!!!");

            //cancel any pending notifications
            alarmManager.cancel(alarmIntent);

            return;
        }
    }

    private static SharedPreferences userPreferences;
    private static final String TAG = "Nadget";

}
