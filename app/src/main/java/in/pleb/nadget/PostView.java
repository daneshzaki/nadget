package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PostView extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        //check network connectivity
        if(!isNetworkAvailable())
        {
            displayNetworkError();
            return;
        }

        //set values of the selected thing in the input fields
        bundle = this.getIntent().getBundleExtra("post");

        Log.i(TAG, "Before loadValues Bundle is " + bundle);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        Log.i(TAG," onBackPressed");

        Intent intent = new Intent(this, NadgetMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //load the values in the screen
    private void loadValues()
    {
        //set the action bar
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3B3131")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //set the actionbar title
        Spannable text = new SpannableString(bundle.getString("title"));
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);

        //change the back arrow color
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        //load the page in webview
        WebView webView = ((WebView) findViewById(R.id.webView));

        webView.setWebViewClient(new WebViewClient());

        if(bundle.getString("link") != null )
        {
            webView.loadUrl(bundle.getString("link"));
        }
        else
        {
            webView.loadUrl("www.pleb.in");
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

    //method to display error on no connection
    public void displayNetworkError()
    {
        Log.i(TAG, "NadgetMain displayError");
        //mainFragment.setError("Please check your network connection and try again");
        //Toast.makeText(this, "Please check your network connection and try again", Toast.LENGTH_LONG).show();
        Snackbar.make(this.findViewById(R.id.webView), "No network connection. Please refresh", Snackbar.LENGTH_LONG).show();
    }
    private static final String TAG = "Nadget PostView";

    //bundle for getting data from main list
    private Bundle bundle = null;

}
