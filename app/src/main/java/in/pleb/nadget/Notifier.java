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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import org.ocpsoft.prettytime.*;

import java.util.Calendar;

/**
 * This class does the following:
 * checks the time the user wants to notify
 * displays notification
 */

public class Notifier {
    public static void process(Context context)
    {

        userPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (userPreferences.getBoolean("notify", false)) {

            int notifyTimeHr = userPreferences.getInt("notifyTimeHr", 0);
            int notifyTimeMin = userPreferences.getInt("notifyTimeMin", 0);
            Log.i(TAG, "Notifier setupListAppearance notifyTimeHr = "+notifyTimeHr);
            Log.i(TAG, "Notifier setupListAppearance notifyTimeMin = "+notifyTimeMin);

            long notifyTime = ((notifyTimeHr*60) + notifyTimeMin) * 60000;

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            //alarmManager.set(AlarmManager.RTC_WAKEUP, notifyTime, alarmIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notifyTime, 86400000,alarmIntent);

        }
        else {

            return;

        }

    }



    private final static String APP_TITLE = "Nadget";

    private static SharedPreferences userPreferences;
    private static final String TAG = "Nadget";

}
