package in.pleb.nadget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

public class SavedFeeds extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_feeds);

        listView = (ListView) findViewById(R.id.listView);
        emptyView = (TextView) findViewById(R.id.empty);
        setupToolbar();
        loadValues();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        Log.i(TAG, "SavedFeeds onItemClick ");

        Bundle bundle = new Bundle();
        // post title
        bundle.putString("title", feedValues[position]);
        // post link
        bundle.putString("link", feedKeys[position]);

        Log.i(TAG, "onListItemClick bundle title = " + bundle.getString("title"));

        // create an intent and add the bundle to it
        Intent displayIntent = new Intent(SavedFeeds.this, PostView.class);

        displayIntent.putExtra("post", bundle);
        startActivity(displayIntent);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Log.i(TAG,"onItemLongClick title = "+feedValues[i]);
        Log.i(TAG,"onItemLongClick link = "+feedKeys[i]);
        //remove or add article
        editor = sharedPreferences.edit();

        //if favorite exists, remove it else add it
        if(sharedPreferences.contains(feedKeys[i]))
        {
            editor.remove(feedKeys[i]);
            Toast.makeText(this, "Article removed from reading list", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //write link, title to shared prefs file
            editor.putString(feedKeys[i],feedValues[i]);
            Toast.makeText(this, "Article saved to reading list", Toast.LENGTH_SHORT).show();
        }

        editor.commit();

        return false;
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
                Spannable text = new SpannableString("Saved Articles");
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);

                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material );
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

    }
    private void loadValues()
    {
        //get prefs to get the values
        sharedPreferences = getSharedPreferences(FAVS_FILE_NAME, Context.MODE_PRIVATE);

        Map<String,?> feeds = (Map<String, String>) sharedPreferences.getAll();
        //check empty
        if(feeds == null || feeds.size()==0)
        {
            Log.i(TAG,"SavedFeeds loadValues feeds empty");

            //show message - no favorites
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        Log.i(TAG,"SavedFeeds loadValues feeds size="+feeds.size());

        //display key and values in a list
        Set<String> feedKeySet = feeds.keySet();
        feedKeys = feedKeySet.toArray(new String[feedKeySet.size()]);

        //Set<? extends Map.Entry<String, ?>> feedValueSet = feeds.entrySet();
        feedValues = feedKeySet.toArray(new String[feedKeySet.size()]);

        for (int i = 0; i <feedKeys.length ; i++)
        {
            Log.i(TAG,"SavedFeeds getting values"+feeds.get(feedKeys[i]));
            feedValues[i]= (String) feeds.get(feedKeys[i]);
        }

        //create main list adapter
        ArrayAdapter adapter = createFavListAdapter(feedKeys);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    private ArrayAdapter createFavListAdapter(final String[] feedKeys)
    {
        ArrayAdapter adapter = new ArrayAdapter(SavedFeeds.this,android.R.layout.simple_list_item_1, feedKeys)
        {
            // layout for two text views
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                TextView tv1, tv2;
                LinearLayout ll, ll2;

                if (convertView == null)
                {
                    Log.i(TAG, "onActivityCreated convertView null");

                    //fav title
                    tv1 = new TextView(getContext());
                    tv1.setTypeface(null, Typeface.BOLD);
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setTextSize(16.0f);
                    tv1.setPadding(10, 15, 10, 15);
                    tv1.setElevation(16.0f);
                    tv1.setTextColor(Color.parseColor("#3b3131"));

                    //fav link
                    tv2 = new TextView(getContext());
                    tv2.setTypeface(null, Typeface.ITALIC);
                    tv2.setGravity(Gravity.LEFT);
                    tv2.setTextSize(12.0f);
                    tv2.setPadding(10, 15, 10, 15);
                    tv1.setElevation(16.0f);

                    ll = new LinearLayout(getContext());
                    ll.setOrientation(LinearLayout.HORIZONTAL);

                    // layout for text views
                    ll2 = new LinearLayout(getContext());
                    ll2.setOrientation(LinearLayout.VERTICAL);
                    ll2.addView(tv1, 0);
                    ll2.addView(tv2, 1);

                    tv1.setText(feedValues[position]);

                    Log.i(TAG, "tv1 setText " + feedValues[position] );
                    Log.i(TAG, "tv2 setText " + feedKeys[position] );

                    tv2.setText(feedKeys[position]);
                    Log.i(TAG, "onActivityCreated convertView null addView");

                    ll.addView(ll2);
                } else
                {
                    Log.i(TAG, "onActivityCreated else");
                    ll = (LinearLayout) convertView;
                    ll2 = (LinearLayout) (ll.getChildAt(0));
                    tv1 = (TextView) (ll2.getChildAt(0));
                    tv2 = (TextView) (ll2.getChildAt(1));

                    tv1.setText(feedValues[position] );
                    tv2.setText(feedKeys[position]);
                }

                return ll;
            }
        };

        return adapter;
    }


    private ActionBar actionBar = null;
    private String[] feedKeys;
    private String[] feedValues;
    private static final String TAG = "Nadget";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String FAVS_FILE_NAME = "in.pleb.nadget.FavoriteFeeds";
    private TextView emptyView;
    private ListView listView;
}
