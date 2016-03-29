package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import java.util.ArrayList;

import com.shirwa.simplistic_rss.*;

import org.xml.sax.SAXException;

public class NadgetMain extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_nadget_main);

        //font
        //set fonts for all text
        typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // set the adapter for the navigation drawer
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemLabels));

        // set the nav drawer list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));
        drawerList.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));

        //set the action bar
        actionBar = getActionBar();
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

        //check network connectivity
        if(!isNetworkAvailable())
        {
            displayNetworkError();
            return;
        }

        //refresh main list
        refreshMainList();
    }

    //on resume
    @Override
    public void onResume()
    {
        super.onResume();
        if(!isNetworkAvailable())
        {
            displayNetworkError();
            return;
        }
        //refresh main list
        refreshMainList();

    }

    @Override
    //on restart
    public void onRestart()
    {
        super.onRestart();
        if(!isNetworkAvailable())
        {
            displayNetworkError();
            return;
        }
        //refresh main list
        refreshMainList();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.i(TAG, "Nav bar item clicked is "+drawerItemLabels[position]);

            //feed selector clicked
            if(position == 2)
            {
                startActivity(new Intent(NadgetMain.this, FeedSelector.class));
            }
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
        protected ArrayList<RssItem> doInBackground(String... urls)
        {
            Log.i(TAG, "NadgetMain doInBg start");
            //for feeds that need setting user agent use the extended parser
            ArrayList<RssItem> rssItems = null;


           /* if(isUserAgentRequired)
            {
                return extendedParse(urls[0]);
            }*/

            try
            {
                rssItems = extendedParse(urls[0]);
                //RssReader reader = new RssReader(urls[0]);
                //rssItems = (ArrayList<RssItem>) reader.getItems();
                //return loadFromNetwork(urls[0]);
            }
            catch (Exception e)
            {
                Log.e(TAG,"doInBg exception"+ e.toString());
                displayInternalError();
            }
            //Log.i(TAG, "***NadgetMain doInBg rssItems = "+rssItems.toString());
            //Log.i(TAG, "NadgetMain doInBg return");
            return rssItems;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(ArrayList<RssItem> rssItems) {
            Log.i(TAG, "***NadgetMain onPostExec rssItems = "+rssItems);
            Log.i(TAG, "***NadgetMain onPostExec rssItems = "+rssItems.toString());

            //set post details
            setPostDetails(rssItems);

            titleArr = titleList.toArray(new String[titleList.size()]);
            linkArr = linkList.toArray(new String[titleList.size()]);
            descriptionArr = descriptionList.toArray(new String[titleList.size()]);
            pubDateArr = pubDateList.toArray(new String[titleList.size()]);
            imageLinkArr = imageLinkList.toArray(new String[titleList.size()]);

            Log.i(TAG, "***NadgetMain onPostExec titleList = "+titleList.toString());

            //create main list adapter
            adapter = createMainListAdapter(titleArr, descriptionArr, linkArr, pubDateArr, imageLinkArr);
            mainFragment.setArrayAdapter(adapter);
        }
    }

    //use extended parser for feeds that require user agent to be set
    private ArrayList<RssItem> extendedParse(String url)
    {
        ArrayList<RssItem> rssItems = null;

        try
        {
            //take the number of posts from settings
            ExtendedRssParser extendedRssParser = new ExtendedRssParser(url, NUMBER_OF_POSTS);
            extendedRssParser.parse();
            rssItems = (ArrayList<RssItem>) extendedRssParser.getItems();
        }
        catch(SAXException e)
        {
            Log.e(TAG,"extendedParse exception "+ e.toString());

            //return feed when limit reached
            if(e.toString().contains("\nLimit reached"))
            {
                Log.e(TAG,"extendedParse limit reached check rssItems="+rssItems);

                return rssItems;
            }

        }
        catch (Exception e)
        {
            Log.e(TAG,"extendedParse exception"+ e.toString());
            displayInternalError();
        }

        return rssItems;
    }


    //set post details
    private void setPostDetails(ArrayList<RssItem> rssItems)
    {
        Log.i(TAG, "*** setPostDetails entry" );

        //clearAll();

        for (RssItem rssItem : rssItems) {
            Log.i(TAG, "*** setPostDetails title: " + rssItem.getTitle());
            setItemTitle(rssItem.getTitle());
            setItemDescription(rssItem.getDescription());
            setItemPubDate(rssItem.getPubDate());
            //Log.i("Nadget", "URL: " + rssItem.getLink());
            setItemLink(rssItem.getLink());
            //Log.i("Nadget", "Image URL: " + rssItem.getImageUrl());
            if(rssItem.getImageUrl() != null)
            {
                setItemLink(rssItem.getImageUrl());
            }
        }
        Log.i(TAG, "*** setPostDetails exit" );


    }

    //create main list adapter
    private ArrayAdapter createMainListAdapter(final String[] titleArr, final String[] descriptionArr,
                                               final String[] linkArr, final String[] pubDateArr,
                                               final String[] imageLinkArr)
    {
        ArrayAdapter adapter = new ArrayAdapter(NadgetMain.this,android.R.layout.two_line_list_item, titleArr)
        {
            // layout for image and two text views
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                //Log.i(TAG, "onActivityCreated getView starting...");
                ImageView iv;
                TextView tv1, tv2;
                LinearLayout ll, ll2;

                if (convertView == null)
                {
                    //Log.i(TAG, "onActivityCreated convertView null");
                    iv = new ImageView(getContext());
                    iv.setPadding(5, 10, 5, 10);
                    //uncomment the line below if thumbnails are of different sizes
                    //iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    tv1 = new TextView(getContext());
                    tv1.setTypeface(typeface, Typeface.BOLD);
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setTextSize(18.0f);
                    tv1.setTextColor(android.graphics.Color.parseColor("#000000"));
                    tv1.setPadding(7, 20, 7, 15);
                    tv1.setLines(2);
                    //tv1.setEllipsize(TextUtils.TruncateAt.END);
                    tv1.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));

                    // second line text view
                    tv2 = new TextView(getContext());
                    tv2.setTypeface(typeface, Typeface.ITALIC);
                    tv2.setGravity(Gravity.LEFT);
                    tv2.setTextSize(14.0f);
                    tv2.setTextColor(android.graphics.Color.parseColor("#000000"));
                    tv2.setPadding(7, 15, 7, 20);
                    tv2.setLines(1);
                    tv2.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));

                    ll = new LinearLayout(getContext());
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));

                    // layout for text views
                    ll2 = new LinearLayout(getContext());
                    ll2.setOrientation(LinearLayout.VERTICAL);
                    ll2.setBackgroundColor(android.graphics.Color.parseColor("#EFEBE9"));
                    ll2.addView(tv1, 0);
                    ll2.addView(tv2, 1);

                    //TODO: display post image
                    iv.setImageResource(R.drawable.ic_action_ng);
                    tv1.setText(titleArr[position]);
                    //Log.i(TAG, "tv1 setText " + titleArr[position]);
                    //Log.i(TAG, "tv1 setText " + pubDateArr[position] );
                    tv2.setText(pubDateArr[position]);
                    //Log.i(TAG, "onActivityCreated convertView null addView");
                    ll.addView(iv);
                    ll.addView(ll2);
                } else
                {
                    //Log.i(TAG, "onActivityCreated else");
                    ll = (LinearLayout) convertView;
                    iv = (ImageView) ll.getChildAt(0);
                    ll2 = (LinearLayout) (ll.getChildAt(1));
                    tv1 = (TextView) (ll2.getChildAt(0));
                    tv2 = (TextView) (ll2.getChildAt(1));

                    // TODO:display post image
                    //Log.i(TAG, "tv1 else setText " + titleArr[position] );
                    //Log.i(TAG,"tv2 else setText "+pubDateArr[position]);

                    tv1.setText(titleArr[position] );
                    tv2.setText(pubDateArr[position]);
                }

                return ll;
            }
        };

        return adapter;
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

    //set published Date
    private void setItemPubDate(String content)
    {
        //Log.i(TAG, "in set description with " + content);
        pubDateList.add(content);
    }

    //set item link
    private void setItemLink(String content)
    {
        //Log.i(TAG, "in set item link with " + content);
        linkList.add(content);

    }

    //clear all content
    private void clearAll()
    {
        titleList.clear();
        linkList.clear();
        descriptionList.clear();
        pubDateList.clear();
        imageLinkList.clear();
    }

    //check network state
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //method to display error on no connection
    public void displayNetworkError()
    {
        Log.i(TAG, "NadgetMain displayNetworkError");
        //mainFragment.setError("Please check your network connection and try again");
        Toast.makeText(this, "Please check your network connection and try again", Toast.LENGTH_LONG).show();
        //Snackbar.make(this.findViewById(R.id.drawer_layout), "Please check your network connection and try again", Snackbar.LENGTH_LONG).show();
    }

    //for all generic errors
    public void displayInternalError() {
        Log.i(TAG, "NadgetMain displayInternalError");
        //mainFragment.setError("Please check your network connection and try again");
        //Toast.makeText(this, "Sorry, an internal error occurred", Toast.LENGTH_SHORT).show();

    }



    //method to refresh
    public void refreshMainList()
    {
        Log.i(TAG, "refreshMainList 1 isRefreshedMainList="+isRefreshedMainList);
        try
        {
            //TODO: remove this after testing with multiple feeds
            //isUserAgentRequired = true;
            if(!isRefreshedMainList)
            {
                Log.i(TAG, "refreshMainList 2 isRefreshedMainList="+isRefreshedMainList);
                isRefreshedMainList = true;
                //TODO: move the block below to a method once settings - feed chooser is ready
                //todo: remove the test block on success

                //begin test block
                //todo:restrict to less posts
                /*for (int i = 0; i < feedList.length; i++)
                {
                    Log.i(TAG,"executing feed"+feedList[i]);
                    new DownloadTask().execute(feedList[i]);
                }*/
                //end test block
                //todo uncomment below after testing
                new DownloadTask().execute(NDTV_NEWS_FEED);
                new DownloadTask().execute(TIMES_FEED);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }
        Log.i(TAG, "refreshMainList 3 isRefreshedMainList="+isRefreshedMainList);
    }

    //method for pull to refresh
    public void refreshForPull()
    {
        Log.i(TAG, "refreshForPull");
        try
        {
            //TODO: remove this after testing with multiple feeds
            //isUserAgentRequired = true;

            //TODO: move the block below to a method once settings - feed chooser is ready
            //todo: remove the test block on success

            //begin test block
            //todo:restrict to less posts
            /*for (int i = 0; i < feedList.length; i++) {
                Log.i(TAG,"executing feed"+feedList[i]);
                new DownloadTask().execute(feedList[i]);
            }*/


            //end test block
            new DownloadTask().execute(NDTV_NEWS_FEED);
            new DownloadTask().execute(TIMES_FEED);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }
    }


    //method to return post title
    public String[] getItemTitle()
    {
        return titleArr;
    }

    public String[] getItemLink()
    {
        return linkArr;
    }
    public String[] getItemDescription()
    {
        return descriptionArr;
    }
    public String[] getItemPubDate()
    {
        return pubDateArr;
    }
    public String[] getItemImageLink()
    {
        return imageLinkArr;
    }


    private static final String TAG = "Nadget Main";
    private static final int POST_TITLE_LENGTH = 40;

    private MainFragment mainFragment;

    private static final String NDTV_NEWS_FEED = "http://gadgets.ndtv.com/rss/news";
    private static final String TIMES_FEED = "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss";

    private String[] drawerItemLabels = new String[]{"Sign in", "Saved Articles", "Select Feeds", "Settings"};
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> descriptionList = new ArrayList<>();
    private ArrayList<String> linkList = new ArrayList<>();
    private ArrayList<String> imageLinkList = new ArrayList<>();
    private ArrayList<String> pubDateList = new ArrayList<>();

    private String[] titleArr = null;
    private String[] linkArr = null;
    private String[] descriptionArr = null;
    private String[] pubDateArr = null;
    private String[] imageLinkArr = null;

    private Typeface typeface = null;
    private ArrayAdapter adapter = null;
    private ActionBar actionBar = null;
    private boolean isUserAgentRequired = false;
    private boolean isRefreshedMainList = false;
    private static final int NUMBER_OF_POSTS = 20;

    //todo: remove after testing
    private static final String[] feedList = new String[]{
            //"http://www.bgr.in/feed/", //problem feed
            //"http://www.firstpost.com/tech/feed", //problem feed
            "http://indianexpress.com/section/technology/feed/",//problem feed
            "http://www.gizmodo.in/rss_section_feeds/23005095.cms" //problem feed
            //,
            /*"http://www.thehindu.com/sci-tech/?service=rss",
            "http://www.digit.in/rss-feed/",
            "http://www.ibtimes.co.in/rss",
            "http://feeds.feedburner.com/igyaan",
            "http://feeds.feedburner.com/Thegeekybyte",
            "http://feeds2.feedburner.com/fone-arena",
            "http://feeds.feedburner.com/ogfeed",
            "http://feeds.feedblitz.com/gogi-technology",
            "http://gadgets.ndtv.com/rss/news",
            "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss",
            "http://www.techtree.com/rss.xml"*/
    };

}
