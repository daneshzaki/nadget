package in.pleb.nadget;

import android.preference.PreferenceManager;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FeedSelector extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //setTheme(android.R.style.Theme_Material_NoActionBar);
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(userPreferences.getBoolean("darkTheme", false))
        {
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_selector);

        //set the action bar
        setupToolbar();

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

        feedMaster.put("http://gadgets.ndtv.com/rss/news","Gadgets 360 News");
        feedMaster.put("http://gadgets.ndtv.com/rss/reviews","Gadgets 360 Reviews");
        feedMaster.put("http://www.bgr.in/feed/", "BGR");
        feedMaster.put("http://timesofindia.indiatimes.com/rssfeeds/5880659.cms","TOI Tech");
        feedMaster.put("http://www.techtree.com/rss.xml","TechTree");
        feedMaster.put("http://feeds.feedburner.com/igyaan","iGyaan" );
        feedMaster.put("http://indianexpress.com/section/technology/feed/", "Indian Express Tech" );
        feedMaster.put("http://www.tribuneindia.com/rss/feed.aspx?cat_id=18","Tribune Tech");
        feedMaster.put("http://www.ibtimes.co.in/rss/technology","IBTimes");
        feedMaster.put("http://www.gizmodo.in/rss_section_feeds/23005095.cms","Gizmodo");
        feedMaster.put("http://feeds.feedburner.com/digit/latest-from-digit","Digit");
        feedMaster.put("http://www.news18.com/rss/tech.xml","News 18 Tech");
        feedMaster.put("http://feeds2.feedburner.com/fone-arena","Fonearena");
        feedMaster.put("http://www.gizbot.com/rss/gizbot-fb.xml","Gizbot");
        feedMaster.put("http://feeds.feedburner.com/ogfeed","Only Gizmos");
        feedMaster.put("http://trak.in/feed/", "Trak.in ");

    }

    //setup feed picker UI
    private void setupUI()
    {
        //values to be displayed in feedpicker
        feedPickerValues = new ArrayList<>();
        feedPickerValues.add(new Item("http://gadgets.ndtv.com/rss/news","Gadgets 360 News"));
        feedPickerValues.add(new Item("http://gadgets.ndtv.com/rss/reviews","Gadgets 360 Reviews"));
        feedPickerValues.add(new Item("http://www.bgr.in/feed/", "BGR"));
        feedPickerValues.add(new Item("http://timesofindia.indiatimes.com/rssfeeds/5880659.cms","TOI Tech"));
        feedPickerValues.add(new Item("http://www.techtree.com/rss.xml","TechTree"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/igyaan","iGyaan" ));
        feedPickerValues.add(new Item("http://indianexpress.com/section/technology/feed/", "Indian Express Tech" ));
        feedPickerValues.add(new Item("http://www.tribuneindia.com/rss/feed.aspx?cat_id=18","Tribune Tech"));
        feedPickerValues.add(new Item("http://www.ibtimes.co.in/rss/technology","IBTimes"));
        feedPickerValues.add(new Item("http://www.gizmodo.in/rss_section_feeds/23005095.cms","Gizmodo"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/digit/latest-from-digit","Digit"));
        feedPickerValues.add(new Item("http://www.news18.com/rss/tech.xml","News 18 Tech"));
        feedPickerValues.add(new Item("http://feeds2.feedburner.com/fone-arena","Fonearena"));
        feedPickerValues.add(new Item("http://www.gizbot.com/rss/gizbot-fb.xml","Gizbot"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/ogfeed","Only Gizmos"));
        feedPickerValues.add(new Item("http://trak.in/feed/", "Trak.in "));


        //setup feed Picker with values
        feedPicker = (CollectionPicker) findViewById(R.id.feed_picker);
        feedPicker.setItems(feedPickerValues);

        feedPicker.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(Item item, int position) {
                Log.i(TAG,"item = "+item.id+" "+item.text+" at position "+position+" clicked");

            }
        });

        //set feed picker button state based on values in shared prefs
        feedPicker.setCheckedItems((HashMap<String, Object>) sharedPreferences.getAll());

    }

    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar ***");
        getWindow().setStatusBarColor(Color.parseColor("#423131"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);

            actionBar = getSupportActionBar();
            Log.i(TAG,"actionbar= "+actionBar);

            if (actionBar != null)
            {
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));

                //set the actionbar title
                Spannable text = new SpannableString("Select Feeds");
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);

                //change the back arrow color
                //final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material );
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha );

                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //android.R.id.home
        if (id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        Log.i(TAG,"FeedSelector onBackPressed");

        //Log.i(TAG,"feedPicker selected items = "+feedPicker.getCheckedItems());
        //clear existing values and add to selectedFeeds
        editor.clear();
        editor.commit();

        Set<String> set = feedPicker.getCheckedItems().keySet();
        Set<Map.Entry<String, Object>> entrySet = feedPicker.getCheckedItems().entrySet();

        Log.i(TAG,"feedPicker selected items keys= "+set.toString());
        //Log.i(TAG,"feedPicker selected items entries= "+entrySet.toString());

        String[] feedKeys = set.toArray(new String[set.size()]);

        for (int i = 0; i <feedKeys.length ; i++)
        {
            Log.i(TAG,"feedPicker add key = "+feedKeys[i]);
            Log.i(TAG,"feedPicker add value = "+feedMaster.get(feedKeys[i]));

            //Log.i(TAG,"FeedSelector setupUI feedKeys"+feedKeys[i]);
            editor.putString(feedKeys[i],feedMaster.get(feedKeys[i]));
        }
        editor.commit();

        Intent intent = new Intent(this, NadgetMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //master list of feeds
    private static HashMap<String,String> feedMaster;

    private static ArrayList<Item> feedPickerValues;
    //to store selected feeds
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CollectionPicker feedPicker;

    private ActionBar actionBar = null;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private static final String TAG = "Nadget";
    private SharedPreferences userPreferences;

}
