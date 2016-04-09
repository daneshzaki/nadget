
package in.pleb.nadget;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;


/**
 * Fragment for displaying the main list with posts
 */
public class MainFragment extends Fragment
{

	public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.main_fragment, container, false);

		// Retrieve the SwipeRefreshLayout and ListView instances
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

		// Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
		swipeRefreshLayout.setColorSchemeResources(R.color.cardview_shadow_start_color, R.color.cardview_dark_background);

		//set the main view
		mainView = (RecyclerView)view.findViewById(R.id.mainView);
		mainView.setHasFixedSize(true); //If you are sure that the size of the RecyclerView won't be changing

		LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
		mainView.setLayoutManager(llm);

		//Log.i(TAG,"mainfragment onCreateView complete");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);

        //setup UI
		mainView.setBackgroundColor(Color.WHITE);

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


	//refresh the list
	private void initiateRefresh()
	{
		Log.i(TAG, "****MainFragment*****");
		Log.i(TAG, "MainFragment initiateRefresh");

		//call asynctask to refresh
		((NadgetMain)(getActivity())).refreshForPull();
		//stop the refreshing indicator
		swipeRefreshLayout.setRefreshing(false);

	}

	//set the adapter for recycle view and the click listener
	public void setAdapter(MainViewAdapter adapter)
	{
		Log.i(TAG, "mainfragment setAdapter");
		mainView.setAdapter(adapter);

		adapter.setOnItemClickListener(
				new MainViewAdapter.ItemClickListener()
				{
					public void onItemClick(int position, View v){
					Log.i(TAG, " Clicked on Item " + position);
						// call webView with post selected
						// create a bundle with values to be passed to display screen
						Bundle bundle = new Bundle();
						ArrayList<RssItem> rssItems = ((NadgetMain) (getActivity())).getItems();
						// post title
						bundle.putString("title", rssItems.get(position).getTitle());
						// post description
						bundle.putString("description", rssItems.get(position).getDescription());
						// post link
						bundle.putString("link", rssItems.get(position).getLink());
						// post date
						bundle.putString("pubDate", rssItems.get(position).getPubDate());
						// post image link
						bundle.putString("imageLink", rssItems.get(position).getImageUrl());

						Log.i("Nadget", "onListItemClick bundle title = " + bundle.getString("title"));

						// create an intent and add the bundle to it
						Intent displayIntent = new Intent(getActivity(), PostView.class);
						displayIntent.putExtra("post", bundle);
						startActivity(displayIntent);

				}});
	}

	//method to display any error
	public void setError(String errorMsg)
	{
		Log.i(TAG, "mainfragment setError");
		//emptyView.setText(errorMsg);
	}

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView mainView = null;
	private static final String TAG = "Nadget MainFragment";

}