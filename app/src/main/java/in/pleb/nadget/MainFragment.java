
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

import android.app.ListFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment for displaying the main list with posts
 */
public class MainFragment extends ListFragment implements AdapterView.OnItemClickListener {

	public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        mainTextView = new TextView(getActivity());

		// Retrieve the SwipeRefreshLayout and ListView instances
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

		// Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
		//swipeRefreshLayout.setColorScheme(R.color.swipe_color_1, R.color.swipe_color_2,R.color.swipe_color_3, R.color.swipe_color_4);

		Log.i(TAG,"mainfragment onCreateView complete");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);

        //setup UI
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setTextFilterEnabled(true);
        getListView().setDivider(new ColorDrawable(Color.LTGRAY));
        getListView().setDividerHeight(1);
		//emptyView = (TextView) getListView().findViewById(android.R.id.empty);
		//Log.i(TAG, "mainfragment emptyView "+emptyView);
		Log.i(TAG, "mainfragment onActivityCreated setupUI complete");
        //layout and load data


        getListView().setOnItemClickListener(this);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

				initiateRefresh();
			}
		});
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        //handle row selects in list - move to fragment
        // call webView with post selected
        // create a bundle with values to be passed to display screen
	/*	Bundle bundle = new Bundle();

		// post title
		bundle.putString("id", thingsArList.get(position).getId());

		// post description
		bundle.putString("name", thingsArList.get(position).getName());

		// post link
		bundle.putString("price", thingsArList.get(position).getPrice());

		// post date
		bundle.putString("purchaseDate", thingsArList.get(position)
				.getPurchaseDate());

		// post image link (check null before setting)
		bundle.putString("picLocation", thingsArList.get(position)
				.getPicLocation());

		Log.i("Nadget",
				"onListItemClick setting bundle pic location = "
						+ bundle.getString("picLocation"));

		// create an intent and add the bundle to it
		Intent displayIntent = new Intent(ThingseActivity.this,
				DetailedPostView.class);

		displayIntent.putExtra("nadget", bundle);

		startActivity(displayIntent);
*/

    }

	//refresh the list
	private void initiateRefresh()
	{
		Log.i(TAG, "****MainFragment*****");
		Log.i(TAG, "MainFragment initiateRefresh");

		//call asynctask to refresh
		((NadgetMain)(getActivity())).refreshMainList();
		//stop the refreshing indicator
		swipeRefreshLayout.setRefreshing(false);


	}

	//set list adapter
	public void setArrayAdapter(ArrayAdapter arrayAdapter)
	{
		this.arrayAdapter = arrayAdapter;
		this.setListAdapter(arrayAdapter);
	}

	//method to display any error
	public void setError(String errorMsg)
	{
		Log.i(TAG, "mainfragment setError");
		//emptyView.setText(errorMsg);
	}

	private ArrayAdapter arrayAdapter;
    private TextView mainTextView;

	//private TextView emptyView;
	private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "Nadget";
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> descriptionList = new ArrayList<>();
    private ArrayList<String> linkList = new ArrayList<>();
    private ArrayList<String> imageLinkArr = new ArrayList<>();

}