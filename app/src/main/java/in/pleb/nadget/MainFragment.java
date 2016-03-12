/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.pleb.nadget;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Fragment used for displaying content using a TextView.
 */
public class MainFragment extends Fragment {

    //TODO:once the code starts working - replace mainTextView with a list view similar to old Thingse
    private TextView mainTextView;
    private ScrollView mScrollView;

    public MainFragment() {}

    public View inflateViews() {
        mScrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mScrollView.setLayoutParams(scrollParams);

        mainTextView = new TextView(getActivity());
        ViewGroup.LayoutParams logParams = new ViewGroup.LayoutParams(scrollParams);
        mainTextView.setLayoutParams(logParams);
        mainTextView.setClickable(false);
        mainTextView.setFocusable(false);
        mainTextView.setBackgroundColor(Color.WHITE);
        mainTextView.setGravity(Gravity.BOTTOM);


        mScrollView.addView(mainTextView);
        return mScrollView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflateViews();

        mainTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        return result;
    }

    //for setting text
    public void setMainText(String content)
    {
        Log.i(TAG, "in set text with " + content);

        mainTextView.append(content);

    }

    //set item title
    public void setItemTitle(String content)
    {
        Log.i(TAG, "in set title with " + content);
        mainTextView.append("\n");
        mainTextView.setTypeface(null, Typeface.BOLD);
        mainTextView.setLinksClickable(false);
        mainTextView.append(content);
        mainTextView.append("\n");

    }

    //set description
    public void setItemDescription(String content)
    {
        Log.i(TAG, "in set description with " + content);

        mainTextView.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        mainTextView.setLinksClickable(true);
        mainTextView.append(content);
        mainTextView.append("\n");

    }

    //set item link
    public void setItemLink(String content)
    {
        Log.i(TAG, "in set item link with " + content);

        mainTextView.setTypeface(null, Typeface.NORMAL);
        mainTextView.setLinksClickable(false);
        mainTextView.append(content);
        mainTextView.append("\n");

    }


    private static final String TAG = "Nadget";

}