package in.pleb.nadget;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;


public class SuggestFeeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_feeds);

        setupToolbar();

        //set fonts for all text
        Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");
        ((TextView)findViewById( R.id.suggestTextView)).setTypeface(typeface, Typeface.BOLD);
        ((TextView)findViewById( R.id.feedNameView)).setTypeface(typeface);
        ((TextView)findViewById( R.id.feedUrlView)).setTypeface(typeface);
        ((TextView)findViewById( R.id.feedDescView)).setTypeface(typeface);

    }

    private void setupToolbar()
    {
        Log.i(TAG,"setupToolbar ***");
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
                Spannable text = new SpannableString("Suggest News Source");
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(text);

                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
                actionBar.setHomeButtonEnabled(true);
            }
        }

    }

    private static final String TAG = "Nadget";


}
