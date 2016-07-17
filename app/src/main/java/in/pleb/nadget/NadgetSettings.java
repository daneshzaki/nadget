package in.pleb.nadget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;


public class NadgetSettings extends PreferenceActivity {
    // this method loads the preferences from the XML file
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        setupToolbar();

        //initialize Dropbox
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dbApi = new DropboxAPI<AndroidAuthSession>(session);

        // about dialog
        Preference aboutPref = (Preference) findPreference("aboutPref");

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
        Preference osPref = (Preference) findPreference("osPref");

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
        Preference exportPref = (Preference) findPreference("export");
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
        Preference importPref = (Preference) findPreference("import");
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

    }

    protected void onResume()
    {
        super.onResume();

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
        Intent intent = new Intent(this, NadgetMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //finish();
        return true;
    }

    //handle back button
    @Override
    public void onBackPressed()
    {
        Log.i(TAG,"onBackPressed");

        //sending back to the main activity
        Intent intent = new Intent(this, NadgetMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    //cleanup
    protected void onDestroy()
    {
        super.onDestroy();
    }


    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar ***");
        ActionBar actionBar = getActionBar();
        if(actionBar!= null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
            actionBar.setTitle(R.string.settings);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

            //change the back arrow color
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material );
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setHomeButtonEnabled(true);

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


}
