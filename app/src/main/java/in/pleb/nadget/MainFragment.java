
package in.pleb.nadget;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

		// Retrieve the SwipeRefreshLayout and ListView instances
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

		// Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
		//swipeRefreshLayout.setColorScheme(R.color.swipe_color_1, R.color.swipe_color_2,R.color.swipe_color_3, R.color.swipe_color_4);

		//Log.i(TAG,"mainfragment onCreateView complete");
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
		getListView().setBackgroundColor(Color.WHITE);
        getListView().setDividerHeight(1);
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

	//handle row selects in list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

        // call webView with post selected
        // create a bundle with values to be passed to display screen
		Bundle bundle = new Bundle();

		// post title
		bundle.putString("title", ((NadgetMain) (getActivity())).getItemTitle()[position]);

		// post description
		bundle.putString("description", ((NadgetMain) (getActivity())).getItemDescription()[position]);

		// post link
		bundle.putString("link", ((NadgetMain) (getActivity())).getItemLink()[position]);

		// post date
		bundle.putString("pubDate", ((NadgetMain) (getActivity())).getItemPubDate()[position]);

		// post image link
		bundle.putString("imageLink", ((NadgetMain) (getActivity())).getItemImageLink()[position]);

		Log.i("Nadget", "onListItemClick bundle title = " + bundle.getString("title"));

		// create an intent and add the bundle to it
		Intent displayIntent = new Intent(getActivity(), PostView.class);

		displayIntent.putExtra("post", bundle);

		startActivity(displayIntent);

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


	//method to display any error
	public void setError(String errorMsg)
	{
		Log.i(TAG, "mainfragment setError");
		//emptyView.setText(errorMsg);
	}

	private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "Nadget MainFragment";

}