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

            try
            {
                ExtendedRssParser extendedRssParser = new ExtendedRssParser(urls[0], NUMBER_OF_POSTS);
                extendedRssParser.parse();
                rssItems.addAll(extendedRssParser.getItems());
            }
            catch (Exception e)
            {
                Log.e(TAG,"doInBg exception"+ e.toString());
                displayInternalError();
            }
            return rssItems;
        }

        /**
         * Set the rss feed posts to the adapter
         */
        @Override
        protected void onPostExecute(ArrayList<RssItem> rssItems) {
            Log.i(TAG, "***NadgetMain onPostExec rssItems = "+rssItems);

            //create main list adapter
            adapter = new MainViewAdapter(NadgetMain.this, rssItems);
            mainFragment.setAdapter(adapter);

            //set feed source

        }
    }

    //clear all content
    private void clearAll()
    {
        rssItems.clear();
    }

    //check network state
    private boolean isNetworkAvailable()
    {
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
        Toast.makeText(this, "Please check your network connection and try again", Toast.LENGTH_LONG).show();
        //Snackbar.make(this.findViewById(R.id.drawer_layout), "Please check your network connection and try again", Snackbar.LENGTH_LONG).show();
    }

    //for all generic errors
    public void displayInternalError() {
        Log.i(TAG, "NadgetMain displayInternalError");
        Toast.makeText(this, "Sorry, an internal error occurred", Toast.LENGTH_SHORT).show();
    }

    //method to refresh
    public void refreshMainList()
    {
        Log.i(TAG, "refreshMainList entry isRefreshedMainList="+isRefreshedMainList);
        try
        {
            if(!isRefreshedMainList)
            {
                isRefreshedMainList = true;
                //todo: get feedlist from feed selector

                for (int i = 0; i < feedList.length; i++)
                {
                    Log.i(TAG,"executing feed"+feedList[i]);
                    new DownloadTask().execute(feedList[i]);
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }
        Log.i(TAG, "refreshMainList exit isRefreshedMainList="+isRefreshedMainList);
    }

    //method for pull to refresh
    public void refreshForPull()
    {
        Log.i(TAG, "refreshForPull");

        //clear the lists
        clearAll();
        try
        {
            //todo:restrict to less posts
            for (int i = 0; i < feedList.length; i++) {
                Log.i(TAG,"executing feed"+feedList[i]);
                new DownloadTask().execute(feedList[i]);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }
    }

    //method to return posts
    public ArrayList<RssItem>  getItems()
    {
        return rssItems;
    }

    private static final String TAG = "Nadget Main";
    private MainFragment mainFragment;

    private String[] drawerItemLabels = new String[]{"Sign in", "Saved Articles", "Select Feeds", "Settings"};
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Typeface typeface = null;
    private MainViewAdapter adapter = null;
    private ActionBar actionBar = null;
    private boolean isRefreshedMainList = false;
    private static final int NUMBER_OF_POSTS = 10;
    private ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
    //todo: remove after testing
    private static final String[] feedList = new String[]{
            //"http://www.bgr.in/feed/", //problem feed
            /*"http://www.firstpost.com/tech/feed",
            "http://indianexpress.com/section/technology/feed/",
            "http://www.gizmodo.in/rss_section_feeds/23005095.cms"
            ,
            "http://www.thehindu.com/sci-tech/?service=rss",
            "http://www.digit.in/rss-feed/", //problem feed
            "http://www.ibtimes.co.in/rss",
            "http://feeds.feedburner.com/igyaan",//problem feed - retest
            "http://feeds.feedburner.com/Thegeekybyte",
            "http://feeds2.feedburner.com/fone-arena",
            "http://feeds.feedburner.com/ogfeed",
            "http://feeds.feedblitz.com/gogi-technology",*/
            "http://gadgets.ndtv.com/rss/news",//working
            "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss"
            //, "http://www.techtree.com/rss.xml"//problem feed
    };

}
