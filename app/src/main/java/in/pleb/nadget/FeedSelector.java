package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FeedSelector extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_selector);
        //set the action bar
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //get prefs to store the values
        sharedPreferences = getSharedPreferences(FEEDS_FILE_NAME, Context.MODE_PRIVATE);

        setupFeedMaster();
        setupUI();

        //setup for writing
        editor = sharedPreferences.edit();
    }

    //initialize feed list
    private static void setupFeedMaster()
    {
        feedMaster = new HashMap<String, String>();

        feedMaster.put("ndtv", "http://gadgets.ndtv.com/rss/news");
        feedMaster.put("bgr", "http://www.bgr.in/feed/");
        feedMaster.put("times", "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss");
        feedMaster.put("techtree", "http://www.techtree.com/rss.xml");
        feedMaster.put("firstpost", "http://www.firstpost.com/tech/feed");
        feedMaster.put("igyaan", "http://feeds.feedburner.com/igyaan");
        feedMaster.put("indianexpress", "http://indianexpress.com/section/technology/feed/");
        feedMaster.put("thehindu", "http://www.thehindu.com/sci-tech/?service=rss");
        feedMaster.put("ibtimes", "http://www.ibtimes.co.in/rss");
        feedMaster.put("gizmodo", "http://www.gizmodo.in/rss_section_feeds/23005095.cms");
        feedMaster.put("digit", "http://www.digit.in/rss-feed/");
        feedMaster.put("thegeekybyte", "http://feeds.feedburner.com/Thegeekybyte");
        feedMaster.put("fonearena", "http://feeds2.feedburner.com/fone-arena");
        feedMaster.put("gogi", "http://feeds.feedblitz.com/gogi-technology");
        feedMaster.put("onlygizmos", "http://feeds.feedburner.com/ogfeed");
    }

    //setup UI
    private void setupUI()
    {
        //set button state based on values in shared prefs
        Set<String> set = feedMaster.keySet();

        String[] feedKeys = (String[]) set.toArray(new String[set.size()]);

        for (int i = 0; i <feedKeys.length ; i++)
        {
            Log.i(TAG,"FeedSelector setupUI feedKeys"+feedKeys[i]);

            if(sharedPreferences.contains(feedKeys[i]))
            {
                Log.i(TAG,"FeedSelector setupUI found "+feedKeys[i]);

                ((ToggleButton)(findViewById(R.id.feedSelectorLinear).findViewWithTag(feedKeys[i]))).setChecked(true);
            }
        }

    }

    //save selected feeds
    public void save(View v)
    {
        //check if its checked
        if(((ToggleButton)v).isChecked())
        {
            //get the tag
            Log.i(TAG,"FeedSelector save selected tag = "+v.getTag());

            //get feed url from feedMaster
            Log.i(TAG,"FeedSelector save selected url = "+feedMaster.get(v.getTag()));

            //add to selectedFeeds
            //TODO: remove selectedFeeds list after sharedprefs works
            //selectedFeeds.add((String) feedMaster.get(v.getTag()));
            editor.putString((String )v.getTag(),((String) feedMaster.get(v.getTag())));
            editor.commit();
        }

        //Log.i(TAG,"FeedSelector save selectedFeeds "+selectedFeeds.toString());
    }
/*
* TODO: 6. Store the selectedfeedlist as preferences/locally
* TODO: x. NadgetMain should use the selectedfeedlist to fetch the feeds on load
* */

    //get selected feeds
    public ArrayList<String> getSelectedFeeds()
    {
        Log.i(TAG,"FeedSelector getSelectedFeeds selectedFeeds "+selectedFeeds.toString());
        return selectedFeeds;
    }

    //set selected feeds
    public void setSelectedFeeds(ArrayList<String> selectedFeeds)
    {
        this.selectedFeeds = selectedFeeds;
    }

    //master list of feeds
    private static HashMap<String,String> feedMaster = null;

    //to store selected feeds
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //selected feeds
    private ArrayList<String> selectedFeeds = new ArrayList<String>();
    private ActionBar actionBar = null;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private static final String TAG = "Nadget FeedSelector";
}
