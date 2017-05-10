package in.pleb.nadget;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;

/**
 * Created by danesh on 10-05-2016.
 * Retrieve image links for the Rss item
 */

public class ImageUrlParser extends AsyncTask<RssItem, Void, RssItem >
{

    @Override
    protected RssItem doInBackground(RssItem... rssItem) {
        Log.i(TAG, "ImageUrlParser doInBg start");

        return parseImageUrl(rssItem[0]);

    }

    @Override
    protected void onPostExecute(RssItem rssItems) {
        Log.i(TAG, "***ImageUrlParser onPostExec rssItems = "+rssItems);

        //update adapter
        adapter.notifyDataSetChanged();

    }

    public void setAdapter(MainViewAdapter adapter)
    {
        this.adapter = adapter;
    }

    //method to parse image from rss feed
    private RssItem parseImageUrl(RssItem rssItem)
    {
        try
        {
            //for instrumentation
            long entryTime = System.currentTimeMillis();

            Log.i(TAG, "ImageUrlParser parseImageUrl link = "+rssItem.getLink().trim());

            if(rssItem.getLink().trim().length() ==0)
            {
                throw new Exception("Invalid Url");
            }

            String imageUrl = ImageExtractor.extractImageUrl(rssItem.getLink().trim());
            Log.i(TAG, "ImageUrlParser image url = \n"+imageUrl);
            rssItem.setImageUrl(imageUrl);
            long exitTime = System.currentTimeMillis();
            long timeTaken = (exitTime - entryTime)/1000;
            Log.i(TAG, "ImageUrlParser  exit time="+timeTaken);

        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e(TAG,"ImageUrlParser "+e.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e(TAG,"ImageUrlParser "+e.toString());
        }

        return rssItem;
    }

    private MainViewAdapter adapter;
    private static final String TAG = "Nadget";

}

