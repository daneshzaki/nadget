package in.pleb.nadget;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class NadgetSettings extends PreferenceActivity {
    // this method loads the preferences from the XML file
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = userPreferences.edit();

        if(userPreferences.getBoolean("darkTheme", false))
        {
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        //initialize Dropbox
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dbApi = new DropboxAPI<AndroidAuthSession>(session);

        // about dialog
        Preference aboutPref = findPreference("aboutPref");

        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                   public boolean onPreferenceClick(Preference preference) {
                                                       Log.i(TAG, "about clicked");

                                                       // display about screen
                                                       startActivity(new Intent(NadgetSettings.this, AboutNadget.class));
                                                       return true;
                                                   }
                                               }

        );

        // open source licenses dialog
        Preference osPref = findPreference("osPref");

        osPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                            {

                                                public boolean onPreferenceClick(Preference preference)
                                                {
                                                    Log.i(TAG, "open src clicked");

                                                    // display licenses screen
                                                    startActivity(new Intent(NadgetSettings.this, OpenSrcLicenses.class));
                                                    return true;
                                                }
                                            }

        );


        // export contents to Dropbox
        Preference exportPref = findPreference("export");
        exportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                                {

                                                    public boolean onPreferenceClick(Preference preference)
                                                    {
                                                        Log.i(TAG, "export clicked");
                                                        dbExport = true;
                                                        dbApi.getSession().startOAuth2Authentication(NadgetSettings.this);
                                                        return true;
                                                    }
                                                }

        );

        // import contents from Dropbox
        Preference importPref = findPreference("import");
        importPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                                {

                                                    public boolean onPreferenceClick(Preference preference)
                                                    {
                                                        Log.i(TAG, "import clicked");
                                                        dbImport = true;
                                                        dbApi.getSession().startOAuth2Authentication(NadgetSettings.this);
                                                        return true;
                                                    }
                                                }
        );

        // share app
        Preference shareAppPref = findPreference("shareApp");

        shareAppPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                   public boolean onPreferenceClick(Preference preference) {
                                                       Log.i(TAG, "share app clicked");
                                                       shareApp();
                                                       return true;
                                                   }
                                               }

        );

        // rate app
        Preference rateAppPref = findPreference("rateApp");

        rateAppPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                 public boolean onPreferenceClick(Preference preference) {
                                                     Log.i(TAG, "rate app clicked");
                                                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.pleb.nadget")));
                                                     return true;
                                                 }
                                             }

        );

        // dark theme
        Preference darkThemePref = findPreference("darkTheme");

        darkThemePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                     public boolean onPreferenceClick(Preference preference) {
                                                         Log.i(TAG, "dark theme clicked");
                                                         finish();
                                                         Intent intent = new Intent(NadgetSettings.this, NadgetSettings.class);
                                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                         startActivity(intent);

                                                         return true;
                                                     }
                                                 }

        );

        //notification time
        notifyTimePref = findPreference("notifyTime");

        notifyTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                    public boolean onPreferenceClick(Preference preference) {
                                                        Log.i(TAG, "notify time clicked");
                                                        //display time picker
                                                        // Get Current Time
                                                        final Calendar c = Calendar.getInstance();
                                                        int curHr = c.get(Calendar.HOUR_OF_DAY);
                                                        int curMin = c.get(Calendar.MINUTE);

                                                        new TimePickerDialog(NadgetSettings.this, timeSetListener, curHr, curMin, false).show();

                                                        return true;
                                                    }
                                                }
        );

        //notifications
        final SwitchPreference notifyPref = (SwitchPreference) findPreference("notify");

        notifyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                       public boolean onPreferenceClick(Preference preference) {
                                                           Log.i(TAG, "notify clicked state="+notifyPref.isChecked());
                                                           //enable notification time pref
                                                           notifyTimePref.setEnabled(notifyPref.isChecked());
                                                           return true;
                                                       }
                                                   }
        );

        //format the notification time and set summary
        formatNotifyTime();

    }

    //for getting notification time
    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selHr = hourOfDay;
                    selMin = minute;
                    Log.i(TAG, "time listenere You will be notified at "+selHr + ":" + selMin);

                    notifyTimePref.setSummary("You will be notified at "+selHr + ":" + selMin);

                    //formatting for MM
                    if(selMin == 0)
                    {
                        notifyTimePref.setSummary("You will be notified at "+selHr + ":00");
                    }

                    //set the time selected in prefs
                    editor.putInt("notifyTimeHr", selHr);
                    editor.putInt("notifyTimeMin", selMin);
                    editor.commit();

                    //build notification
                    Notifier.process(NadgetSettings.this);

                }


            };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#423131"));

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        //change the back arrow color
        bar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        Spannable text = new SpannableString("Settings");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        bar.setTitle(text);

        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sending back to the prev activity
                Log.i(TAG, "calling main");
                Intent intent = new Intent(NadgetSettings.this, NadgetMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onResume()
    {
        super.onResume();

        //format the notification time and set summary
        formatNotifyTime();

        if (dbApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                dbApi.getSession().finishAuthentication();
                Log.i(TAG, "onResume auth success");
                if(dbExport)
                {
                    Log.i(TAG, "onResume calling upload task");
                    new UploadTask().execute();
                }

                if(dbImport)
                {
                    Log.i(TAG, "onResume calling download task");
                    new DownloadTask().execute();
                }

                String accessToken = dbApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }
    // show previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        // sending back to the prev activity
        Log.i(TAG, "calling main");
        Intent intent = new Intent(this, NadgetMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        // sending back to the prev activity
        Log.i(TAG, "calling main");
        Intent intent = new Intent(this, NadgetMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();

    }

    //cleanup
    protected void onDestroy()
    {
        super.onDestroy();
    }

    //this method formats the notification time to add 00 to MM
    private void formatNotifyTime()
    {
        //disable notify time if notifications are disabled
        if(!userPreferences.getBoolean("notify", false))
        {
            //disable notify time pref
            if(notifyTimePref!=null)
            {
                notifyTimePref.setEnabled(userPreferences.getBoolean("notify", false));
            }
        }
        else
        {
            //set the time from prefs
            if(notifyTimePref!=null) {
                if (userPreferences.getInt("notifyTimeMin", 0) < 10) {
                    Log.i(TAG, "NAdgetSEttings format set");

                    notifyTimePref.setSummary("You will be notified at " + userPreferences.getInt("notifyTimeHr", 0) + ":0"+ userPreferences.getInt("notifyTimeMin", 0));
                } else {
                    Log.i(TAG, "NAdgetSEttings no format set");
                    notifyTimePref.setSummary("You will be notified at " + userPreferences.getInt("notifyTimeHr", 0) + ":" + userPreferences.getInt("notifyTimeMin", 0));
                }
            }
        }

    }

    /**
     * Implementation of AsyncTask to upload file to dropbox
     */
    private class UploadTask extends AsyncTask<Void, Void, Boolean >
    {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            Log.i(TAG, "UploadTask doInBg start");

            try
            {
                exportToDropbox();
            }
            catch (Exception e)
            {
                Log.e(TAG,"UploadTask exception "+ e.toString());
                return false;
            }
            return true;
        }

        /**
         * Check status
         */
        @Override
        protected void onPostExecute(Boolean status) {
            Log.i(TAG, "***UploadTask onPostExec ");

            if(status)
            {
                Snackbar.make(getListView(), "Feeds & saved articles exported successfully", Snackbar.LENGTH_LONG).show();
            }
            else
            {
                Snackbar.make(getListView(), "Error exporting to Dropbox", Snackbar.LENGTH_LONG).show();
            }


        }
    }

    /**
     * Implementation of AsyncTask to download file to dropbox
     */
    private class DownloadTask extends AsyncTask<Void, Void, Boolean >
    {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            Log.i(TAG, "DownloadTask doInBg start");

            try
            {
                importFromDropbox();
            }
            catch (Exception e)
            {
                Log.e(TAG,"DownloadTask exception "+ e.toString());
                return false;
            }
            return true;
        }

        /**
         * Check status
         */
        @Override
        protected void onPostExecute(Boolean status)
        {
            Log.i(TAG, "***DownloadTask onPostExec ");
            if(status)
            {
                Snackbar.make(getListView().getSelectedView(), "Feeds & saved articles imported successfully", Snackbar.LENGTH_LONG).show();
            }
            else
            {
                Snackbar.make(getListView(), "Error importing from Dropbox", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private void importFromDropbox()
    {
        Log.i(TAG,"import from Dropbox ");

        FileOutputStream outputStreamSelFeeds = null;
        FileOutputStream outputStreamSavedArt = null;

        try
        {
            //download selected feeds
            File sharedPrefsPath = new File(SHARED_PREFS_PATH);
            File selectedFeedsFile = new File(SHARED_PREFS_PATH + FEEDS_FILE_NAME);
            Log.i(TAG, "shared prefs path.exists()? " + sharedPrefsPath.exists());
            if(!sharedPrefsPath.exists())
            {
                boolean pathCreated = sharedPrefsPath.mkdirs();
                Log.i(TAG, "pathCreated? " + sharedPrefsPath.getPath());
            }

            Log.i(TAG, "selectedFeedsFile.exists()? " + selectedFeedsFile.exists());
            if(!selectedFeedsFile.exists())
            {
                boolean fileCreated = selectedFeedsFile.createNewFile();
                Log.i(TAG, "fileCreated? " + fileCreated);
            }

            outputStreamSelFeeds = new FileOutputStream(selectedFeedsFile);
            DropboxAPI.DropboxFileInfo infoSelFeeds = dbApi.getFile(FEEDS_FILE_NAME, null, outputStreamSelFeeds, null);
            Log.i(TAG, "The downloaded selected fields file's rev is: " + infoSelFeeds.getMetadata().rev);

            //download saved articles
            File savedArticlesFile = new File(SHARED_PREFS_PATH + FAVS_FILE_NAME);
            outputStreamSavedArt = new FileOutputStream(savedArticlesFile);
            DropboxAPI.DropboxFileInfo infoSavedArt = dbApi.getFile(FAVS_FILE_NAME, null, outputStreamSavedArt, null);
            Log.i(TAG, "The downloaded saved articlesfile's rev is: " + infoSavedArt.getMetadata().rev);
            dbImport = false;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        finally {
            try {
                if (outputStreamSelFeeds != null) {
                    outputStreamSelFeeds.close();
                }

                if (outputStreamSavedArt != null) {
                    outputStreamSavedArt.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    private void exportToDropbox()
    {
        Log.i(TAG,"exportToDropbox");

        FileInputStream inputStreamSelFeeds = null;
        FileInputStream inputStreamSavedArt = null;

        try
        {
            //upload saved articles
            File savedArticlesFile = new File(SHARED_PREFS_PATH + FAVS_FILE_NAME);
            inputStreamSavedArt = new FileInputStream(savedArticlesFile);
            DropboxAPI.UploadRequest reqSavedArt = dbApi.putFileOverwriteRequest(FAVS_FILE_NAME, inputStreamSavedArt,
                    savedArticlesFile.length(), null);
            reqSavedArt.upload();
            Log.i(TAG, "Upload Saved Articles successful ");

            //upload selected feeds
            File selectedFeedsFile = new File(SHARED_PREFS_PATH + FEEDS_FILE_NAME);
            inputStreamSelFeeds = new FileInputStream(selectedFeedsFile);
            DropboxAPI.UploadRequest reqSelFeeds = dbApi.putFileOverwriteRequest(FEEDS_FILE_NAME, inputStreamSelFeeds,
                    selectedFeedsFile.length(), null);
            reqSelFeeds.upload();
            Log.i(TAG, "Upload Selected Files successful ");

            dbExport = true;

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        finally {
            try
            {
                if(inputStreamSelFeeds != null)
                {
                    inputStreamSelFeeds.close();
                }

                if(inputStreamSavedArt != null)
                {
                    inputStreamSavedArt.close();
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }

    }

    public void shareApp()
    {
        Log.i(TAG, "shareApp");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, APP_SHARE_TEXT);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share)));

    }

    private DropboxAPI<AndroidAuthSession> dbApi;

    private boolean dbExport = false;

    private boolean dbImport = false;

    private static final String TAG = "Nadget";
    final static private String APP_KEY = "gmxms24fwnzvnub";
    final static private String APP_SECRET = "mqz4z4gzk7dxmjr";

    ///Format: data/data/com.your.package/shared_prefs/com.your.package_preferences.xml
    private static final String SHARED_PREFS_PATH="/data/data/in.pleb.nadget/shared_prefs/";
    private static final String FAVS_FILE_NAME = "in.pleb.nadget.FavoriteFeeds.xml";
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds.xml";

    //app share text
    private static final String APP_SHARE_TEXT = "Checkout my app Nadget for cool tech news " +
            "- https://play.google.com/store/apps/details?id=in.pleb.nadget&rdid=in.pleb.nadget";

    private SharedPreferences userPreferences;
    private SharedPreferences.Editor editor;

    private Preference notifyTimePref;

    private int selHr, selMin;

}
