package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import com.shirwa.simplistic_rss.*;

public class NadgetMain extends Activity{
    //TODO: cleanup to remove proprietary code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_nadget_main);

        //drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemLabels));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        //handle instance state when app closes
        if (savedInstanceState == null) {
            //handle instance state when exit
        }

        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
        //new DownloadTask().execute(NDTV_NEWS_FEED);
        new DownloadTask().execute(TIMES_FEED);
        new DownloadTask().execute(GREENBOT_FEED);


    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.i(TAG, "Nav bar item clicked");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //handle back button
	@Override
	public void onBackPressed()
	{
		Log.i("Nadget","onBackPressed");

		 Intent intent = new Intent(Intent.ACTION_MAIN);
		 intent.addCategory(Intent.CATEGORY_HOME);
		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 startActivity(intent);
		 finish();
	}


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        Log.i(TAG, "onPrepareOptionsMenu");
        //boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        //TODO: hide actionbar items and make drawer appear over actionbar
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, ArrayList<RssItem> > {

        @Override
        protected ArrayList<RssItem> doInBackground(String... urls) {
            ArrayList<RssItem> rssItems = null;

            try {
                RssReader reader = new RssReader(urls[0]);
                rssItems = (ArrayList<RssItem>) reader.getItems();

                //return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                //return getString(R.string.connection_error);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return rssItems;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(ArrayList<RssItem> rssItems) {
            Log.i(TAG, "---------------------------");
            //Log.i(TAG, result);
            for (RssItem rssItem : rssItems) {
                //Log.i("Nadget", "title: " + rssItem.getTitle());
                setItemTitle(rssItem.getTitle());
                //content.append("Title: " + rssItem.getTitle());
                //content.append("\n");
                //Log.i("Nadget", "Description: " + rssItem.getDescription());
                setItemDescription(rssItem.getDescription());
                //content.append("\n");
                //Log.i("Nadget", "URL: " + rssItem.getLink());
                setItemLink(rssItem.getLink());
                //content.append("\n");
                //Log.i("Nadget", "Image URL: " + rssItem.getImageUrl());
                if(rssItem.getImageUrl() != null)
                {
                    setItemLink(rssItem.getImageUrl());
                }
            }

            final String[] titleArr = titleList.toArray(new String[titleList.size()]);
            final String[] linkArr = linkList.toArray(new String[titleList.size()]);
            final String[] descriptionArr = descriptionList.toArray(new String[titleList.size()]);

            Log.i(TAG, "-------------begin arrays--------------");

            //begin debug
            for(int i=0;i<titleArr.length;i++)
            {
                Log.i(TAG,titleArr[i]);
            }

            for(int i=0;i<linkArr.length;i++)
            {
                Log.i(TAG,linkArr[i]);
            }
            for(int i=0;i<descriptionArr.length;i++)
            {
                Log.i(TAG,descriptionArr[i]);
            }
            Log.i(TAG, "-------------end arrays--------------");

            ArrayAdapter adapter = new ArrayAdapter(NadgetMain.this,android.R.layout.simple_list_item_1, titleArr)
            {
                // layout for image and two text views
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    Log.i(TAG, "onActivityCreated getView starting...");
                    ImageView iv;
                    TextView tv1, tv2;
                    LinearLayout ll, ll2;

                    if (convertView == null)
                    {
                        Log.i(TAG, "onActivityCreated convertView null");
                        iv = new ImageView(getContext());
                        iv.setPadding(5, 10, 5, 10);
                        //uncomment the line below if thumbnails are of different sizes
                        //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        tv1 = new TextView(getContext());
                        tv1.setTypeface(null, Typeface.BOLD);
                        tv1.setGravity(Gravity.LEFT);
                        tv1.setTextSize(18.0f);
                        //tv1.setTextColor(android.graphics.Color.parseColor("#33B5E5"));
                        tv1.setPadding(5, 10, 5, 10);
                        tv1.setLines(1);

                        // second line text view
                        tv2 = new TextView(getContext());
                        tv2.setGravity(Gravity.LEFT);
                        tv2.setTextSize(14.0f);
                        tv2.setPadding(5, 10, 5, 10);
                        tv2.setLines(1);

                        ll = new LinearLayout(getContext());
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        ll.setBackgroundColor(android.graphics.Color.parseColor("#fff3f3f3"));

                        // layout for text views
                        ll2 = new LinearLayout(getContext());
                        ll2.setOrientation(LinearLayout.VERTICAL);
                        ll2.setBackgroundColor(android.graphics.Color.parseColor("#fff3f3f3"));
                        ll2.addView(tv1, 0);
                        ll2.addView(tv2, 1);

                        //TODO: display post image
                        Log.i(TAG, "***setText***");
                        tv1.setText(titleArr[position]);
                        Log.i(TAG, "tv1 setText " + titleArr[position]);
                        Log.i(TAG,"tv2 setText "+descriptionArr[position]);
                        Log.i(TAG,"tv2 setText "+linkArr[position]);
                        tv2.setText(descriptionArr[position] + " |\t\t"+ linkArr[position]);
                        Log.i(TAG, "onActivityCreated convertView null addView");
                        ll.addView(iv);
                        ll.addView(ll2);
                    } else
                    {
                        Log.i(TAG, "onActivityCreated else");
                        ll = (LinearLayout) convertView;
                        iv = (ImageView) ll.getChildAt(0);
                        ll2 = (LinearLayout) (ll.getChildAt(1));
                        tv1 = (TextView) (ll2.getChildAt(0));
                        tv2 = (TextView) (ll2.getChildAt(1));

                        // TODO:display post image
                        Log.i(TAG, "***setText***");
                        Log.i(TAG, "tv1 else setText " + titleArr[position]);
                        Log.i(TAG,"tv2 else setText "+descriptionArr[position]);
                        Log.i(TAG,"tv2 else setText "+linkArr[position]);

                        tv1.setText(titleArr[position]);
                        tv2.setText(descriptionArr[position] + " |\t\t"+ linkArr[position]);
                    }

                    return ll;
                }
            };

            mainFragment.setArrayAdapter(adapter);
        }
    }

    private String downloadUrl(String urlString)
    {
        StringBuffer content = new StringBuffer();
        try {
            RssReader reader = new RssReader(urlString);
            ArrayList<RssItem> rssItems = (ArrayList<RssItem>) reader.getItems();
            for (RssItem rssItem : rssItems) {
                Log.i("Nadget", "title: " + rssItem.getTitle());
                content.append("Title: " + rssItem.getTitle());
                content.append("\n");
                content.append("Description: "+rssItem.getDescription());
                content.append("\n");
                content.append("URL: "+rssItem.getLink());
                content.append("\n");
                content.append("Image URL: "+rssItem.getImageUrl());
                content.append("\n");
                content.append("\n");
            }
        }
        catch(IOException e){
            Log.e(TAG,e.toString());
        }
        catch(SAXException e){
            Log.e(TAG,e.toString());
        }
        catch(Exception e){
            Log.e(TAG,e.toString());
        }
        return content.toString();
    }

    //set item title
    private void setItemTitle(String content)
    {
        //Log.i(TAG, "in set title with " + content);
        titleList.add(content);
    }

    //set description
    private void setItemDescription(String content)
    {
        //Log.i(TAG, "in set description with " + content);
        descriptionList.add(content);
    }

    //set item link
    private void setItemLink(String content)
    {
        //Log.i(TAG, "in set item link with " + content);
        linkList.add(content);

    }


    private static final String TAG = "Nadget";
    private MainFragment mainFragment;
    //private String startingUrl = "https://news.google.com/news/section?q=oneplus+review+ndtv&output=rss";
    //private final String NDTV_NEWS_FEED = "http://gadgets.ndtv.com/rss/news";
    //private final String NDTV_NEWS_FEED = "https://feeds.feedburner.com/NDTV-Tech";
    //private final String BGR_FEED = "http://www.bgr.in/feed/";
    private final String TIMES_FEED = "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss";
    //private final String TECHTREE_FEED = "http://www.techtree.com/rss.xml";
    private final String GREENBOT_FEED = "http://www.greenbot.com/index.rss";
    private String[] drawerItemLabels = new String[]{"Sign in", "Saved Articles", "Help", "Settings"};
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> descriptionList = new ArrayList<>();
    private ArrayList<String> linkList = new ArrayList<>();
    private ArrayList<String> imageLinkArr = new ArrayList<>();


}
