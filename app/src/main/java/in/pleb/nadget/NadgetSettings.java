package in.pleb.nadget;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.text.TextUtils;
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
        this.getWindow().setNavigationBarColor(Color.parseColor("#D0D0D0"));

        addPreferencesFromResource(R.xml.settings);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle("Settings");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setHomeButtonEnabled(true);

        // handle preference actions

        // about dialog
        Preference aboutPref = (Preference) findPreference("aboutPref");

        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                                                   public boolean onPreferenceClick(Preference preference) {
                                                       Log.i("Preferences", "about clicked");

                                                       // display about screen
                                                       //startActivity(new Intent(NadgetSettings.this, AboutNadget.class));
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
                                                    //startActivity(new Intent(NadgetSettings.this, OpenSrcLicenses.class));
                                                    return true;
                                                }
                                            }

        );


        // export contents to file
        Preference exportPref = (Preference) findPreference("export");
        exportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                                {

                                                    public boolean onPreferenceClick(Preference preference)
                                                    {
                                                        Log.i("Preferences", "export clicked");
                                                        //display toast with status
                                                        Toast.makeText(getBaseContext(), "Contents successfully exported ", Toast.LENGTH_SHORT).show();
                                                        return true;
                                                    }
                                                }

        );

        // import contents from file
        Preference importPref = (Preference) findPreference("import");
        importPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
                                                {

                                                    public boolean onPreferenceClick(Preference preference)
                                                    {
                                                        Log.i("Preferences", "import clicked");


                                                        //display toast with status
                                                        Toast.makeText(getBaseContext(), "Contents imported successfully", Toast.LENGTH_SHORT).show();

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

}
