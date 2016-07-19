package in.pleb.nadget;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;


public class AboutNadget extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(Color.parseColor("#fff3f3f3"));
        setContentView(R.layout.activity_about_nadget);
        //set fonts for all text
        Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "AgencyFB-Bold.ttf");
        ((TextView)findViewById( R.id.title)).setTypeface(typeface, Typeface.BOLD);
        typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        ((TextView)findViewById( R.id.description)).setTypeface(typeface);
        ((TextView)findViewById( R.id.version)).setTypeface(typeface);


    }


}
