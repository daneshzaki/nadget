package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import com.shirwa.simplistic_rss.*;

public class NadgetMain extends Activity {
    //TODO: cleanup to remove proprietary code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        mainFragment =
                (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
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
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nadget_main, menu);
        return true;
    }
*/
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
                Log.i("Nadget", "title: " + rssItem.getTitle());
                mainFragment.setItemTitle(rssItem.getTitle());
                //content.append("Title: " + rssItem.getTitle());
                //content.append("\n");
                Log.i("Nadget", "Description: " + rssItem.getDescription());
                mainFragment.setItemDescription(rssItem.getDescription());
                //content.append("\n");
                Log.i("Nadget", "URL: " + rssItem.getLink());
                mainFragment.setItemLink(rssItem.getLink());
                //content.append("\n");
                Log.i("Nadget", "Image URL: " + rssItem.getImageUrl());
                if(rssItem.getImageUrl() != null)
                {
                    mainFragment.setItemLink(rssItem.getImageUrl());
                }
            }
        }
    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";

        try {
            //stream = downloadUrl(urlString);
            str = downloadUrl(urlString);
            //str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
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
}
