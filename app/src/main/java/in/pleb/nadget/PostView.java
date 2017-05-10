package in.pleb.nadget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PostView extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(userPreferences.getBoolean("darkTheme", false))
        {
            setTheme(android.R.style.Theme_Material_NoActionBar);
        }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        //set values of the selected thing in the input fields
        bundle = this.getIntent().getBundleExtra("post");

        Log.i(TAG, "Before loadValues Bundle is " + bundle);

        typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        setupToolbar();

        //check network connectivity
        if(!isNetworkAvailable())
        {
            displayNetworkError();
            return;
        }


        //load the thing values on the screen
        loadValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_favorite)
        {
            saveFavorite();
            return true;
        }

        if(id == R.id.action_share)
        {
            shareLink();
            return true;
        }

        if(id == R.id.action_noimages)
        {
            toggleImages(item);
            return true;
        }

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
        Log.i(TAG, "PostView onBackPressed");
        finish();
    }

    //load the values in the screen
    private void loadValues()
    {
        //load the page in webview
        webView = ((WebView) findViewById(R.id.webView));

        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setJavaScriptEnabled(true);

        //May  2017
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setStandardFontFamily("SourceSansPro-Regular");
        webView.getSettings().setBlockNetworkImage(blockImages);
        webView.getSettings().setSerifFontFamily("SourceSansPro-Regular");
        webView.getSettings().setCursiveFontFamily("SourceSansPro-Regular");
        webView.getSettings().setSansSerifFontFamily("SourceSansPro-Regular");
        webView.getSettings().setFantasyFontFamily("SourceSansPro-Regular");
        webView.getSettings().setFixedFontFamily("SourceSansPro-Regular");
        webView.getSettings().setUserAgentString("Android");

        if(bundle.getString("link") != null )
        {
            link = bundle.getString("link");
            webView.loadUrl(link);
            title = bundle.getString("title");
        }
        else
        {
            webView.loadUrl("www.pleb.in");
        }

    }

    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar ***");
        getWindow().setStatusBarColor(Color.parseColor("#423131"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);

            final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            Log.i(TAG,"actionbar= "+actionBar);

            if (actionBar != null)
            {
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
                actionBar.setDisplayHomeAsUpEnabled(true);

                //set the actionbar title
                Spannable text = new SpannableString(bundle.getString("title"));
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);

                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha );
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
                actionBar.setHomeButtonEnabled(true);
            }
        }

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

    //save favorite
    private void saveFavorite()
    {
        favoriteSet = true;
        Log.i(TAG, "PostView saveFavorite");
        //get prefs to store the values
        sharedPreferences = getSharedPreferences(FAVS_FILE_NAME, Context.MODE_PRIVATE);

        //setup for writing
        editor = sharedPreferences.edit();

        //if favorite exists, remove it else add it
        if(sharedPreferences.contains(link))
        {
            editor.remove(link);
            Toast.makeText(this, "Article removed from reading list", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //write link, title to shared prefs file
            editor.putString(link,title);
            Toast.makeText(this, "Article saved to reading list", Toast.LENGTH_SHORT).show();
        }

        editor.commit();

        Log.i(TAG, "PostView saveFavorite : link - title "+link +" - "+title);
    }

    //show share dialog
    private void shareLink()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title +"\n "+link);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share)));
    }

    //toggle image loading
    private void toggleImages(MenuItem item)
    {
        String imageMsg = "";

        if(blockImages)
        {
            item.setIcon(R.drawable.ic_no_camera_100);
            imageMsg = "Showing images ";
            blockImages = false;
        }
        else
        {
            item.setIcon(R.drawable.ic_photo_camera);
            imageMsg = "Images blocked";
            blockImages = true;
        }

        Snackbar.make(this.findViewById(R.id.webView), imageMsg, Snackbar.LENGTH_SHORT).show();
        webView.getSettings().setBlockNetworkImage(blockImages);
        webView.reload();
    }

    //method to display error on no connection
    public void displayNetworkError()
    {
        Log.i(TAG, "PostView displayError");
        //mainFragment.setError("Please check your network connection and try again");
        //Toast.makeText(this, "Please check your network connection and try again", Toast.LENGTH_LONG).show();
        Snackbar.make(this.findViewById(R.id.webView), "No network connection. Please refresh", Snackbar.LENGTH_LONG).show();
    }
    private static final String TAG = "Nadget";

    //bundle for getting data from main list
    private Bundle bundle = null;
    private String link = "";
    private String title = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean favoriteSet = false;
    private static final String FAVS_FILE_NAME = "in.pleb.nadget.FavoriteFeeds";
    private SharedPreferences userPreferences;
    private Typeface typeface = null;
    private boolean blockImages = false;
    private WebView webView;

}
