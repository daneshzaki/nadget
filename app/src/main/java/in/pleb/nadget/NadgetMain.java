package in.pleb.nadget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;





public class NadgetMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setupListAppearance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nadget_main);

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        navDrawLayout = (RelativeLayout) findViewById(R.id.left_drawer_content_layout);
        navDrawLayout.setBackgroundColor(Color.WHITE);
        drawerLogo = (ImageView) findViewById(R.id.image_view);

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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
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

        //build notification
        Notifier.process(this);

        //request users to rate the app
        AppRater.app_launched(this);
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

    public void closeDrawer(){
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.i(TAG, "Nav bar item clicked is "+drawerItemLabels[position]);

            //{"Saved Articles", "Select Feeds", "Suggest Feeds", "Settings"};

            //saved articles clicked
            if(position == 0)
            {
                startActivity(new Intent(NadgetMain.this, SavedFeeds.class));
                closeDrawer();
                drawerToggle.syncState();
            }
            //feed selector clicked
            if(position == 1)
            {
                startActivity(new Intent(NadgetMain.this, FeedSelector.class));
                closeDrawer();
                drawerToggle.syncState();
            }
            //suggest feeds clicked
            if(position == 2)
            {
                startActivity(new Intent(NadgetMain.this, SuggestFeeds.class));
                closeDrawer();
                drawerToggle.syncState();
            }
            //settings clicked
            if(position == 3)
            {
                startActivity(new Intent(NadgetMain.this, NadgetSettings.class));
                closeDrawer();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i(TAG,"onPrepareOptionsMenu ");

        MenuItem item = menu.findItem(R.id.action_darktheme);

        Log.i(TAG,"onPrepareOptionsMenu darktheme sel* "+userPreferences.getBoolean("darkTheme", false));

        //inverse title like Google news
        if(userPreferences.getBoolean("darkTheme", false))
        {
            item.setTitle("Light Theme");
        }
        else
        {
            item.setTitle("Dark Theme");
        }

        Log.i(TAG,"onPrepareOptionsMenu item title = "+item.getTitle());

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i(TAG,"onCreateOptionsMenu ");

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_darktheme);

        Log.i(TAG,"onCreateOptionsMenu darktheme sel* "+userPreferences.getBoolean("darkTheme", false));

        //inverse title like Google news
        if(userPreferences.getBoolean("darkTheme", false))
        {
            item.setTitle("Light Theme");
        }
        else
        {
            item.setTitle("Dark Theme");
        }

        Log.i(TAG,"onCreateOptionsMenu item title = "+item.getTitle());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.i(TAG,"onOptionsItemSelected settings sel");
                startActivity(new Intent(NadgetMain.this, NadgetSettings.class));
                return true;

            case R.id.action_darktheme:
                Log.i(TAG,"onOptionsItemSelected darktheme sel ");
                //rec the selection
                editor = userPreferences.edit();

                //show dark theme
                Log.i(TAG,"onOptionsItemSelected darktheme sel* "+item.isChecked());
                //editor.putBoolean("darkTheme", true);
                if(item.getTitle().equals("Dark Theme"))
                {
                    Log.i(TAG,"onOptionsItemSelected darktheme 1st if");
                    editor.putBoolean("darkTheme", true);
                }
                else
                {
                    Log.i(TAG,"onOptionsItemSelected darktheme 2nd if");
                    //show light theme
                    editor.putBoolean("darkTheme", false);

                }

                editor.commit();


                //reload the activity
                finish();
                Intent intent = new Intent(NadgetMain.this, NadgetMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar");

        getWindow().setStatusBarColor(Color.parseColor("#423131"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Spannable text = new SpannableString("Nadget");
        //text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            //toolbar.setTitleTextAppearance(this, R.style.ToolBarTextStyle);
            //toolbar.setSubtitleTextColor(Color.WHITE);
            toolbar.setBackground(new ColorDrawable(Color.parseColor("#3B3131")));

            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setBackgroundColor(Color.parseColor("#3B3131"));
            //collapsingToolbar.setTitleEnabled(false);


            actionBar = getSupportActionBar();
            Log.i(TAG,"actionbar= "+actionBar);

            if (actionBar != null)
            {
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
                //set the actionbar titleSelected
                TextView titleView = new TextView(getBaseContext());
                Typeface font = Typeface.createFromAsset(getResources().getAssets(),"AgencyFB-Bold.ttf");
                titleView.setTypeface(font);
                titleView.setTextSize(36.0f);
                titleView.setText(R.string.logo);
                titleView.setTextColor(Color.WHITE);

                actionBar.setCustomView(titleView);

                //actionBar.setTitle(R.string.app_name);
                //actionBar.setLogo(R.drawable.nadget_logo_white_large);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setDisplayShowCustomEnabled(true);

                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_drawer);
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    //setup main list view - background and image loading
    private void setupListAppearance()
    {
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "setupListAppearance darkTheme = "+userPreferences.getBoolean("darkTheme", false));
        if(userPreferences.getBoolean("darkTheme", false))
        {
            Log.i(TAG, "setupListAppearance setting dark theme");
            setTheme(android.R.style.Theme_Material_NoActionBar);
            if(navDrawLayout!= null)
            {
                navDrawLayout.setBackgroundColor(Color.DKGRAY);
                drawerLogo.setImageResource(R.drawable.nadget_drawer_logo_white);
            }
        }
        else
        {
            Log.i(TAG, "setupListAppearance setting light theme");
            setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            if(navDrawLayout!= null)
            {
                navDrawLayout.setBackgroundColor(Color.WHITE);

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

            Log.i(TAG, "***NadgetMain onPostExec refreshing = "+mainFragment.getSwipeRefreshLayout().isRefreshing());
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
        snackbarNetwork.setAction("Refresh", new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "NadgetMain Refresh");
            refreshMainList();
        }
    });
        snackbarNetwork.show();
    }

    //for all generic errors
    public void displayInternalError() {
        Log.i(TAG, "NadgetMain displayInternalError");

        snackbarInternal = Snackbar.make(this.findViewById(R.id.content_frame), "Internal Error. Please refresh", Snackbar.LENGTH_INDEFINITE);
        snackbarInternal.setAction("Refresh", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "NadgetMain Refresh");
                refreshMainList();
            }
        });

        snackbarInternal.show();
    }

    //method to refresh
    public void refreshMainList()
    {
        setupListAppearance();
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

        //to handle network issues
        if(feedKeys != null)
        {
            Log.i(TAG, "refreshForPull not null");
            feedIndex = feedKeys.length -1;
            new RssReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) feedKeys[feedIndex].trim());
        }
        else
        {
            Log.i(TAG, "refreshForPull null ");
            mainFragment.getSwipeRefreshLayout().setRefreshing(false);
            Log.i(TAG, "refreshForPull stopping refresh");
            noFeedsSelected = true;
            return;
        }

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

            Map<String,?> feeds = sharedPreferences.getAll();

            //check empty
            if(feeds == null || feeds.size()==0)
            {
                Log.i(TAG,"NadgetMain refreshCore feeds empty");

                //hide recycler view to show empty/loading message
                //mainFragment.setMainViewVisible(false);

                //set empty message and show
                emptyView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
                noFeedsSelected = true;
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

    //indicate no feeds selected to stop refresh
    public boolean isNoFeedsSelected()
    {
        return noFeedsSelected;
    }

    private static final String TAG = "Nadget";
    private MainFragment mainFragment;

    private String[] drawerItemLabels = new String[]{"Saved Articles", "Select Feeds", "Suggest Feeds", "Settings"};
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private RelativeLayout navDrawLayout;
    private ListView drawerList;
    private ImageView drawerLogo;

    private MainViewAdapter adapter = null;
    private android.support.v7.app.ActionBar actionBar;
    private boolean isRefreshedMainList = false;
    private static final int NUMBER_OF_POSTS = 10;
    private ArrayList<RssItem> rssItems = new ArrayList<RssItem>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences userPreferences;
    private static final String FEEDS_FILE_NAME = "in.pleb.nadget.SelectedFeeds";
    private Snackbar snackbarNetwork;
    private Snackbar snackbarInternal;

    private ImageView emptyView;
    private com.wang.avi.AVLoadingIndicatorView loadingView;

    //all feeds from shared prefs
    private String[] feedKeys;

    //current executing feed index
    private int feedIndex = 0;

    //set on no feeds selected
    private boolean noFeedsSelected = false;

    //to store dark theme
    private SharedPreferences.Editor editor;

    private Menu toolbarMenu;
}
