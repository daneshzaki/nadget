package in.pleb.nadget;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by danesh on 03-04-2016.
 */
//public class MainViewAdapter  extends RecyclerView.Adapter<MainViewAdapter.PostViewHolder>
public class MainViewAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public MainViewAdapter(AppCompatActivity activity, ArrayList<RssItem> rssItems)
    {
        this.activity = activity;
        this.rssItems = rssItems;
        Log.i(TAG,"MainViewAdapter rssItems size ="+rssItems.size());

        //initialize the drawables
        setupFeedDrawables();

        //set fonts for all text
        typeface = Typeface.createFromAsset( this.activity.getResources().getAssets(), "SourceSansPro-Regular.otf");
    }

    //for header
    public static class HeaderHolder extends RecyclerView.ViewHolder{

        TextView headerText;
        ImageView headerImage;

        HeaderHolder(View itemView)
        {
            super(itemView);
            headerText = (TextView)itemView.findViewById(R.id.headerText);
            headerImage = (ImageView)itemView.findViewById(R.id.headerImage);
        }
    }

    //for rows
    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView postTitleView;
        TextView postDateView;
        ImageView postImageView;

        PostViewHolder(View itemView)
        {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);

            postTitleView = (TextView)itemView.findViewById(R.id.postTitle);
            postDateView = (TextView)itemView.findViewById(R.id.postDate);
            postImageView = (ImageView)itemView.findViewById(R.id.postImage);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            itemClickListener.onItemClick(getPosition(),view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_header, parent, false);
            HeaderHolder holder = new HeaderHolder(v);
            return holder;
        }
        else if (viewType == TYPE_ITEM)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_view_row, parent, false);
            PostViewHolder holder = new PostViewHolder(v);
            return holder;
        }
        else
        {
            Log.i(TAG,"no header and no row!!!!!!!!!!");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof PostViewHolder)
        {
            ((PostViewHolder)holder).postTitleView.setText(rssItems.get(position).getTitle());
            ((PostViewHolder)holder).postDateView.setText(rssItems.get(position).getPubDate());
            ((PostViewHolder)holder).postTitleView.setTypeface(typeface, Typeface.BOLD);
            ((PostViewHolder)holder).postDateView.setTypeface(typeface, Typeface.ITALIC);

            //TODO: set post image - change to image from rss item post
            if(feedDrawables.containsKey(rssItems.get(position).getSource()))
            {
                ((PostViewHolder)holder).postImageView.setImageResource(
                        feedDrawables.get(rssItems.get(position).getSource()));
            }
            //use setScaleType in image view to size images
        }
        else if(holder instanceof HeaderHolder)
        {
            Log.i(TAG,"MainViewAdapter onBindViewHolder headerholder pos="+position);
            ((HeaderHolder)holder).headerText.setTypeface(typeface);
        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        //Log.i(TAG,"MainViewAdapter getItemCount size="+rssItems.size());
        return rssItems.size() ; //+1 for header?
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    public interface ItemClickListener
    {
        public void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    //set rss items when available
    public void setItems(ArrayList<RssItem> rssItems)
    {
        this.rssItems = rssItems;
    }

    //initialize feed drawables
    private static void setupFeedDrawables()
    {
        feedDrawables = new HashMap<String, Integer>();

        feedDrawables.put("http://gadgets.ndtv.com/rss/news", R.drawable.ndtv);
        feedDrawables.put("http://www.bgr.in/feed/", R.drawable.bgr);
        feedDrawables.put("http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss", R.drawable.times);
        feedDrawables.put("http://www.techtree.com/rss.xml", R.drawable.techtree);
        feedDrawables.put("http://feeds.feedburner.com/igyaan", R.drawable.igyaan);
        feedDrawables.put("http://indianexpress.com/section/technology/feed/", R.drawable.indianexpress);
        feedDrawables.put("http://www.thehindu.com/sci-tech/?service=rss", R.drawable.thehindu);
        feedDrawables.put("http://www.ibtimes.co.in/rss", R.drawable.ibtimes);
        feedDrawables.put("http://www.gizmodo.in/rss_section_feeds/23005095.cms", R.drawable.gizmodo);
        feedDrawables.put("http://www.digit.in/rss-feed/", R.drawable.digit);
        feedDrawables.put("http://feeds.feedburner.com/Thegeekybyte", R.drawable.thegeekybyte);
        feedDrawables.put("http://feeds2.feedburner.com/fone-arena", R.drawable.fonearena);
        feedDrawables.put("https://www.gogi.in/feed", R.drawable.gogi);
        feedDrawables.put("http://feeds.feedburner.com/ogfeed", R.drawable.onlygizmos);

    }


    private Typeface typeface = null;
    private AppCompatActivity activity = null;
    private ArrayList<RssItem> rssItems;

    private static ItemClickListener itemClickListener = null;
    private static final String TAG = "Nadget Main";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private static HashMap<String,Integer> feedDrawables = null;

}
