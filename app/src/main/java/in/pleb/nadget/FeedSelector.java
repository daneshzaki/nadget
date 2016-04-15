package in.pleb.nadget;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Set;

public class FeedSelector extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_selector);
        //set the action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
        //set the actionbar title
        Spannable text = new SpannableString("Nadget");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //change the back arrow color
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

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
        feedMaster.put("igyaan", "http://feeds.feedburner.com/igyaan");
        feedMaster.put("indianexpress", "http://indianexpress.com/section/technology/feed/");
        feedMaster.put("thehindu", "http://www.thehindu.com/sci-tech/?service=rss");
        feedMaster.put("ibtimes", "http://www.ibtimes.co.in/rss");
        feedMaster.put("gizmodo", "http://www.gizmodo.in/rss_section_feeds/23005095.cms");
        feedMaster.put("digit", "http://www.digit.in/rss-feed/");
        feedMaster.put("thegeekybyte", "http://feeds.feedburner.com/Thegeekybyte");
        feedMaster.put("fonearena", "http://feeds2.feedburner.com/fone-arena");
        feedMaster.put("gogi", "https://www.gogi.in/feed");
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
            //Log.i(TAG,"FeedSelector setupUI feedKeys"+feedKeys[i]);

            if(sharedPreferences.contains(feedKeys[i]))
            {
                Log.i(TAG,"FeedSelector setupUI found "+feedKeys[i]);

                ((ToggleButton)(findViewById(R.id.feedSelectorLinear).findViewWithTag(feedKeys[i]))).setChecked(true);
            }
        }

    }
    public void onBackPressed()
    {
        Log.i(TAG,"FeedSelector onBackPressed");

        Intent intent = new Intent(this, NadgetMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //save selected feeds
    public void save(View v)
    {
        //add if its checked
        if(((ToggleButton)v).isChecked())
        {
            //get the tag
            Log.i(TAG,"FeedSelector save selected add tag = "+v.getTag());

            //get feed url from feedMaster
            Log.i(TAG,"FeedSelector save selected add url = "+feedMaster.get(v.getTag()));

            //add to selectedFeeds
            editor.putString((String )v.getTag(),((String) feedMaster.get(v.getTag())));
            editor.commit();
        }
        else
        {
            //remove from selectedFeeds
            Log.i(TAG,"FeedSelector save selected remove tag = "+v.getTag());

            editor.remove((String )v.getTag());
            editor.commit();
        }
    }

    //master list of feeds
    private static HashMap<String,String> feedMaster = null;

    //to store selected feeds
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ActionBar actionBar = null;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private static final String TAG = "Nadget FeedSelector";
}
