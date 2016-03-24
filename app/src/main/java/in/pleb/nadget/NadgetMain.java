package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.shirwa.simplistic_rss.*;

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
        protected ArrayList<RssItem> doInBackground(String... urls)
        {
            Log.i(TAG, "NadgetMain doInBg start");
            //for feeds that need setting user agent use the extended parser
            ArrayList<RssItem> rssItems = null;

            if(isUserAgentRequired)
            {
                return extendedParse();
            }

            try {
                RssReader reader = new RssReader(urls[0]);
                rssItems = (ArrayList<RssItem>) reader.getItems();
                //return loadFromNetwork(urls[0]);
            } catch (IOException e)
            {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                displayNetworkError();
                //return getString(R.string.connection_error);
            } catch (Exception e)
            {
                Log.e(TAG, e.toString());
                displayInternalError();
                return null;
            }
            //Log.i(TAG, "***NadgetMain doInBg rssItems = "+rssItems.toString());
            Log.i(TAG, "NadgetMain doInBg return");
            return rssItems;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(ArrayList<RssItem> rssItems) {
            //Log.i(TAG, "***NadgetMain onPostExec rssItems = "+rssItems.toString());

            //set post details
            setPostDetails(rssItems);

            titleArr = titleList.toArray(new String[titleList.size()]);
            linkArr = linkList.toArray(new String[titleList.size()]);
            descriptionArr = descriptionList.toArray(new String[titleList.size()]);
            pubDateArr = pubDateList.toArray(new String[titleList.size()]);
            imageLinkArr = imageLinkList.toArray(new String[titleList.size()]);

            Log.i(TAG, "***NadgetMain onPostExec titleList = "+titleList.toString());

            for(int i=0;i<titleArr.length;i++)
            {
                //Log.i(TAG, "***NadgetMain onPostExec titleArr = "+titleArr[i]);
            }

            //create main list adapter
            adapter = createMainListAdapter(titleArr, descriptionArr, linkArr, pubDateArr, imageLinkArr);
            mainFragment.setArrayAdapter(adapter);
        }
    }

    //use extended parser for feeds that require user agent to be set
    private ArrayList<RssItem> extendedParse()
    {
        ArrayList<RssItem> rssItems = null;

        try
        {
            ExtendedRssParser extendedRssParser = new ExtendedRssParser(NDTV_NEWS_FEED, 100);
            extendedRssParser.parse();
            rssItems = (ArrayList<RssItem>) extendedRssParser.getItems();
        }
        catch(IOException e)
        {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            displayNetworkError();
            return null;
            //return getString(R.string.connection_error);
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
            displayInternalError();
            return null;
        }

        return rssItems;
    }


    //set post details
    private void setPostDetails(ArrayList<RssItem> rssItems)
    {
        Log.i(TAG, "*** setPostDetails entry" );

        clearAll();

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
                    tv1.setEllipsize(TextUtils.TruncateAt.END);
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
        //check size and display ellipsis for long titles

        if(content.length() > POST_TITLE_LENGTH)
        {
            content.substring(0, (POST_TITLE_LENGTH - 3));
            content.concat("...");
        }

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
        Log.i(TAG, "refreshMainList");
        try
        {
            //new DownloadTask().execute(TIMES_FEED);

            //set this for feeds that need it
            isUserAgentRequired = true;
            new DownloadTask().execute(NDTV_NEWS_FEED);
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
    //private String startingUrl = "https://news.google.com/news/section?q=oneplus+review+ndtv&output=rss";
    private final String NDTV_NEWS_FEED = "http://gadgets.ndtv.com/rss/news";
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
}
