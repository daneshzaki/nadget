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
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        feedMaster.put("http://gadgets.ndtv.com/rss/news","ndtv");
        feedMaster.put("http://www.bgr.in/feed/", "bgr");
        feedMaster.put("http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss","times");
        feedMaster.put("http://www.techtree.com/rss.xml","techtree");
        feedMaster.put("http://feeds.feedburner.com/igyaan","igyaan" );
        feedMaster.put("http://indianexpress.com/section/technology/feed/", "indianexpress" );
        feedMaster.put("http://www.thehindu.com/sci-tech/?service=rss","thehindu");
        feedMaster.put("http://www.ibtimes.co.in/rss","ibtimes");
        feedMaster.put("http://www.gizmodo.in/rss_section_feeds/23005095.cms","gizmodo");
        feedMaster.put("http://www.digit.in/rss-feed/","digit");
        feedMaster.put("http://feeds.feedburner.com/Thegeekybyte","thegeekybyte");
        feedMaster.put("http://feeds2.feedburner.com/fone-arena","fonearena");
        feedMaster.put("https://www.gogi.in/feed","gogi");
        feedMaster.put("http://feeds.feedburner.com/ogfeed","onlygizmos");
        

    }

    //setup UI
    private void setupUI()
    {
        //values to be displayed in feedpicker
        //TODO: Change case of display values
        feedPickerValues = new ArrayList<>();
        feedPickerValues.add(new Item("http://gadgets.ndtv.com/rss/news","ndtv"));
        feedPickerValues.add(new Item("http://www.bgr.in/feed/", "bgr"));
        feedPickerValues.add(new Item("http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss","times"));
        feedPickerValues.add(new Item("http://www.techtree.com/rss.xml","techtree"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/igyaan","igyaan" ));
        feedPickerValues.add(new Item("http://indianexpress.com/section/technology/feed/", "indianexpress" ));
        feedPickerValues.add(new Item("http://www.thehindu.com/sci-tech/?service=rss","thehindu"));
        feedPickerValues.add(new Item("http://www.ibtimes.co.in/rss","ibtimes"));
        feedPickerValues.add(new Item("http://www.gizmodo.in/rss_section_feeds/23005095.cms","gizmodo"));
        feedPickerValues.add(new Item("http://www.digit.in/rss-feed/","digit"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/Thegeekybyte","thegeekybyte"));
        feedPickerValues.add(new Item("http://feeds2.feedburner.com/fone-arena","fonearena"));
        feedPickerValues.add(new Item("https://www.gogi.in/feed","gogi"));
        feedPickerValues.add(new Item("http://feeds.feedburner.com/ogfeed","onlygizmos"));

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
        Set<String> set = feedMaster.keySet();

        String[] feedKeys = (String[]) set.toArray(new String[set.size()]);

        feedPicker.setCheckedItems((HashMap<String, Object>) sharedPreferences.getAll());
        /*for (int i = 0; i <feedKeys.length ; i++)
        {
            Log.i(TAG,"FeedSelector setupUI feedKeys"+feedKeys[i]);

            if(sharedPreferences.contains(feedKeys[i]))
            {
                Log.i(TAG,"FeedSelector setupUI found "+feedKeys[i]);


            }
        }*/


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

        String[] feedKeys = (String[]) set.toArray(new String[set.size()]);

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
    //save selected feeds
    /*public void save(View v)
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
    }*/

    //master list of feeds
    private static HashMap<String,String> feedMaster;

    private static ArrayList<Item> feedPickerValues;
    //to store selected feeds
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CollectionPicker feedPicker;

    private ActionBar actionBar = null;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private static final String TAG = "Nadget FeedSelector";
}
