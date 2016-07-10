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
import android.text.method.ScrollingMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by danesh on 9/11/2015.
 */
public class OpenSrcLicenses extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensrc_licenses);
        setupToolbar();

        //open src licenses
        String licenses[] = {"\nIcons by Icons8 \nhttps://icons8.com\n",
                "\njsoup \nhttps://jsoup.org\n ",
        "\nThe MIT License (MIT)\n",
        "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), " ,
                "to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense," ,
                " and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:" ,
                " The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. " ,
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " ,
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, " ,
                        "WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE." ,
                "\n\nFoursquare-CollectionPicker \nhttps://github.com/anton46/Foursquare-CollectionPicker\n Copyright (c) 2015 Anton Nurdin Tuhadiansyah \n",
                "\nPicasso \nhttp://square.github.io/picasso\n Copyright (c) 2013 Square, Inc. \n",
                "\nPrettyTime \nhttp://www.ocpsoft.org/prettytime/\n Copyright (c) ocpSoft \n",
                "\nThe Apache License \n",
                "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at " ,
                "http://www.apache.org/licenses/LICENSE-2.0",
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" " ,
                        "BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. " ,
            "See the License for the specific language governing permissions and limitations under the License."};

        //set fonts for all text
        Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

        TextView licenseView = ((TextView) findViewById(R.id.licenseView));

        licenseView.setTypeface(typeface);

        //join all strings
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < licenses.length; i++)
        {
            builder.append(licenses[i]);
        }

        licenseView.setText(builder.toString());
        licenseView.setMovementMethod(new ScrollingMovementMethod().getInstance());
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
                Spannable text = new SpannableString("          ");
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                actionBar.setTitle(R.string.title_activity_licenses);

                //change the back arrow color
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material );
                upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
                actionBar.setHomeButtonEnabled(true);
            }
        }

    }
    private static final String TAG = "Nadget";
}

