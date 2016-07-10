package in.pleb.nadget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;
import com.wang.avi.AVLoadingIndicatorView;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class NadgetMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nadget_main);

        //set fonts for all text
        typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // set the adapter for the navigation drawer
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemLabels));

        // set the nav drawer list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        //set the action bar main_toolbar
        setupToolbar();

        emptyView = (ImageView) findViewById(R.id.empty_view);
        loadingView = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loading_view);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                //actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Nadget</font>"));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        //handle instance state when app closes
        if (savedInstanceState == null) {
            //handle instance state when exit
        }
        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);

        //create main list adapter
        adapter = new MainViewAdapter(NadgetMain.this, rssItems);
        mainFragment.setAdapter(adapter);

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

            //saved articles clicked
            if(position == 1)
            {
                startActivity(new Intent(NadgetMain.this, SavedFeeds.class));
                drawerToggle.syncState();
            }
            //feed selector clicked
            if(position == 2)
            {
                startActivity(new Intent(NadgetMain.this, FeedSelector.class));
                drawerToggle.syncState();
            }
            //settings clicked
            if(position == 3)
            {
                startActivity(new Intent(NadgetMain.this, NadgetSettings.class));
                drawerToggle.syncState();
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
        //Log.i(TAG, "onPrepareOptionsMenu");
        //boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
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

    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar");

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        Spannable text = new SpannableString("Nadget");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            //toolbar.setTitleTextAppearance(this, R.style.ToolBarTextStyle);
            //toolbar.setSubtitleTextColor(Color.WHITE);
            toolbar.setBackground(new ColorDrawable(Color.parseColor("#3B3131")));

            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setBackgroundColor(Color.parseColor("#3B3131"));
            collapsingToolbar.setTitleEnabled(false);


            actionBar = getSupportActionBar();
            Log.i(TAG,"actionbar= "+actionBar);

            if (actionBar != null)
            {
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));

                //set the actionbar title
                actionBar.setTitle(R.string.app_name);
                //actionBar.setSubtitle("News about Gadgets");
                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_drawer);
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class RssReaderTask extends AsyncTask<String, Void, ArrayList<RssItem> >
    {
        @Override
        protected void onPreExecute()
        {
            Log.i(TAG, "NadgetMain onPreExecute");
            mainFragment.getSwipeRefreshLayout().setRefreshing(true);

        }

        @Override
        protected ArrayList<RssItem> doInBackground(String... urls)
        {
            Log.i(TAG, "NadgetMain doInBg start");

            try
            {
                ExtendedRssParser extendedRssParser = new ExtendedRssParser(urls[0], NUMBER_OF_POSTS);
                extendedRssParser.setAdapter(adapter);
                extendedRssParser.parse();
                rssItems.addAll(extendedRssParser.getItems());
            }
            catch (ConnectException e)
            {
                Log.e(TAG,"doInBg connect exception "+ e.toString());

                if(e.getMessage().contains("Connection timed out"))
                {
                    Log.e(TAG,"doInBg connection timed out for a feed.... ignoring");
                    return rssItems;
                }

                displayNetworkError();
            }
            catch (Exception e)
            {
                Log.e(TAG,"doInBg exception "+ e.toString());
                e.printStackTrace();
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

            //show recycler view to show empty/loading message
            mainFragment.setMainViewVisible(true);

            //hide loading message
            loadingView.setVisibility(View.GONE);

            mainFragment.getSwipeRefreshLayout().setRefreshing(false);
            //update adapter
            adapter.notifyDataSetChanged();

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
        snackbarNetwork = Snackbar.make(this.findViewById(R.id.content_frame), "No network connection. Please refresh", Snackbar.LENGTH_INDEFINITE);
        snackbarNetwork.show();
        //TODO: add button to refresh
    }

    //for all generic errors
    public void displayInternalError() {
        Log.i(TAG, "NadgetMain displayInternalError");

        snackbarInternal = Snackbar.make(this.findViewById(R.id.content_frame), "Internal Error. Please restart the app", Snackbar.LENGTH_INDEFINITE);
        snackbarInternal.show();
        //TODO: add button to restart
    }

    //method to refresh
    public void refreshMainList()
    {
        Log.i(TAG, "refreshMainList entry isRefreshedMainList="+isRefreshedMainList);
            if(!isRefreshedMainList)
            {
                isRefreshedMainList = true;
                refreshCore();
            }
        Log.i(TAG, "refreshMainList exit isRefreshedMainList="+isRefreshedMainList);
    }

    //method for pull to refresh
    public void refreshForPull()
    {
        Log.i(TAG, "refreshForPull");
        dismissSnackbars();

        //clear the lists
        clearAll();
        //refreshCore();
        //refresh the first displayed feed
        //new RssReaderTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (String) feedKeys[feedKeys.length-1].trim());
        feedIndex = feedKeys.length -1;
        new RssReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) feedKeys[feedIndex].trim());

    }

    /*
    * Load more logic implemented in refreshCore and refreshMore methods
    * Get all feeds from shared prefs file
    * Count the number of entries from selected feeds- feed size
    * 1. Initial load
    * feed index = n-1
    * call RssReaderTask with last feed (feed index)
    * 2. on load more
    * feed index - 1
    * check if i>0, then call RssReaderTask with feed index

    * */

    private void refreshCore()
    {
        try
        {
            dismissSnackbars();

            //set loading message and show
            loadingView.setVisibility(View.VISIBLE);

            //hide recycler view to show empty/loading message
            mainFragment.setMainViewVisible(false);

            //get prefs to get the values
            sharedPreferences = getSharedPreferences(FEEDS_FILE_NAME, Context.MODE_PRIVATE);

            Map<String,?> feeds = (Map<String, String>) sharedPreferences.getAll();

            //check empty
            if(feeds == null || feeds.size()==0)
            {
                Log.i(TAG,"NadgetMain refreshCore feeds empty");

                //hide recycler view to show empty/loading message
                //mainFragment.setMainViewVisible(false);

                //set empty message and show
                emptyView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);

                //Toast.makeText(this, R.string.no_data_available, Toast.LENGTH_LONG).show();
                return;
            }

            //load more logic begin
            Set<String> set = feeds.keySet();
            feedKeys = set.toArray(new String[set.size()]);
            feedIndex = feedKeys.length -1;
            Log.i(TAG,"NadgetMain refreshCore feedKeys length = "+feedKeys.length);
            Log.i(TAG,"NadgetMain refreshCore executing "+feedKeys[feedIndex]);

            //execute first key
            new RssReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) feedKeys[feedIndex].trim());

        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }

    }

    public void refreshMore()
    {
        try
        {
            Log.i(TAG,"NadgetMain refreshMore feedIndex init= "+feedIndex);

            feedIndex = feedIndex -1;
            Log.i(TAG,"NadgetMain refreshMore feedIndex= "+feedIndex);

            //if only feed is selected no work to be done
            if(feedIndex <0)
            {
                return;
            }

            Log.i(TAG,"NadgetMain refreshMore executing "+feedKeys[feedIndex]);

            //execute the feed
            new RssReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) feedKeys[feedIndex].trim());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
        }

    }


    //dismiss Snackbars
    private void dismissSnackbars()
    {
        if(snackbarInternal!= null && snackbarInternal.isShown())
            snackbarInternal.dismiss();

        if(snackbarNetwork!= null && snackbarNetwork.isShown())
            snackbarNetwork.dismiss();

    }

    //method to return posts
    public ArrayList<RssItem>  getItems()
    {
        return rssItems;
    }

    private static final String TAG = "Nadget";
    private MainFragment mainFragment;

    private String[] drawerItemLabels = new String[]{"Sign in", "Saved Articles", "Select Feeds", "Settings"};
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Typeface typeface;
    private MainViewAdapter adapter = null;
    private android.support.v7.app.ActionBar actionBar;
    private boolean isRefreshedMainList = false;
    private static final int NUMBER_OF_POSTS = 10;
    private ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
    private SharedPreferences sharedPreferences;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private Snackbar snackbarNetwork;
    private Snackbar snackbarInternal;

    private ImageView emptyView;
    private com.wang.avi.AVLoadingIndicatorView loadingView;

    //all feeds from shared prefs
    private String[] feedKeys;

    //current executing feed index
    private int feedIndex = 0;


}
