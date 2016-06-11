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
import android.widget.Toast;

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

        // handle preference actions

        // suggest feeds
        Preference suggestPref = (Preference) findPreference("suggestFeeds");

        suggestPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                   public boolean onPreferenceClick(Preference preference) {
                                                       Log.i("Preferences", "suggest clicked");

                                                       // display about screen
                                                       startActivity(new Intent(NadgetSettings.this, SuggestFeeds.class));
                                                       return true;
                                                   }
                                               }

        );

        // about dialog
        Preference aboutPref = (Preference) findPreference("aboutPref");

        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                   public boolean onPreferenceClick(Preference preference) {
                                                       Log.i("Preferences", "about clicked");

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
                                                    Log.i("Preferences", "open src clicked");

                                                    // display about screen
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
                                                        Log.i("Preferences", "export clicked");
                                                        //display toast with status
                                                        //TODO:export to Dropbox
                                                        //Toast.makeText(getBaseContext(), "Feeds & saved articles exported successfully", Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(getListView(), "Feeds & saved articles exported successfully", Snackbar.LENGTH_LONG).show();
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
                                                        Log.i("Preferences", "import clicked");

                                                        //display toast with status
                                                        //TODO:import from Dropbox
                                                        //Toast.makeText(getBaseContext(), "Feeds & saved articles imported successfully", Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(getListView(), "Feeds & saved articles imported successfully", Snackbar.LENGTH_LONG).show();
                                                        return true;
                                                    }
                                                }
        );

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
        Log.i("Preferences","onBackPressed");

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
            actionBar.setTitle("Settings");
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

            //change the back arrow color
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setHomeButtonEnabled(true);

        }


    }

    private static final String TAG = "Nadget";

}
