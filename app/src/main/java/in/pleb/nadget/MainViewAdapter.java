package in.pleb.nadget;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by danesh on 03-04-2016.
 */
//public class MainViewAdapter  extends RecyclerView.Adapter<MainViewAdapter.PostViewHolder>
public class MainViewAdapter  extends RecyclerView.Adapter<MainViewAdapter.PostViewHolder>
{
    public MainViewAdapter(AppCompatActivity activity, ArrayList<RssItem> rssItems)
    {
        this.activity = activity;
        this.rssItems = rssItems;
        Log.i(TAG,"MainViewAdapter rssItems size ="+rssItems.size());

        //initialize the drawables
        //setupFeedDrawables();
        setupFeedNames();
        //set fonts for all text
        typeface = Typeface.createFromAsset( this.activity.getResources().getAssets(), "SourceSansPro-Regular.otf");
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
    public MainViewAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_view_row, parent, false);
        PostViewHolder holder = new PostViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MainViewAdapter.PostViewHolder holder, int position)
    {
        holder.postTitleView.setText(rssItems.get(position).getTitle());
        Log.i(TAG,"MainViewAdapter onBindViewHolder position="+position);

        //append feed name to date
        if(feedNames.containsKey(rssItems.get(position).getSource()))
        {
            holder.postDateView.setText(feedNames.get(
                    rssItems.get(position).getSource()) + rssItems.get(position).getPubDate());
        }

        holder.postTitleView.setTypeface(typeface, Typeface.BOLD);
        holder.postDateView.setTypeface(typeface, Typeface.ITALIC);

        Log.i(TAG,"MainViewAdapter imgurl = "+rssItems.get(position).getImageUrl());
        if(rssItems.get(position).getImageUrl()!=null && rssItems.get(position).getImageUrl().trim().length()>0)
        {
            Picasso.with(activity.getBaseContext())
                    .load(rssItems.get(position).getImageUrl())
                    .placeholder(R.drawable.spinner)
                    .error(R.drawable.ic_action_ng)
                    .resize(1000, 500)
                    .centerCrop()
                    .into(holder.postImageView);

        }
        else
        {
            Picasso.with(activity.getBaseContext())
                    .load(R.drawable.ic_action_ng)
                    .into(holder.postImageView);
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
    private static void setupFeedNames()
    {
        feedNames = new HashMap<String, String>();

        feedNames.put("http://gadgets.ndtv.com/rss/news", "Gadgets 360 News ");
        feedNames.put("http://gadgets.ndtv.com/rss/reviews", "Gadgets 360 Reviews ");
        feedNames.put("http://www.bgr.in/feed/", "BGR ");
        feedNames.put("http://timesofindia.indiatimes.com/rssfeeds/5880659.cms", "TOI Tech ");
        feedNames.put("http://www.techtree.com/rss.xml", "Techtree ");
        feedNames.put("http://feeds.feedburner.com/igyaan", "iGyaan ");
        feedNames.put("http://indianexpress.com/section/technology/feed/", "Indian Express Tech ");
        feedNames.put("http://www.tribuneindia.com/rss/feed.aspx?cat_id=18","Tribune Tech ");
        feedNames.put("http://www.ibtimes.co.in/rss/technology", "IBTimes ");
        feedNames.put("http://www.gizmodo.in/rss_section_feeds/23005095.cms", "Gizmodo ");
        feedNames.put("http://feeds.feedburner.com/digit/latest-from-digit", "Digit ");
        feedNames.put("http://www.news18.com/rss/tech.xml","News 18 Tech ");
        feedNames.put("http://feeds2.feedburner.com/fone-arena","Fonearena ");
        feedNames.put("http://www.gizbot.com/rss/gizbot-fb.xml", "Gizbot ");
        feedNames.put("http://feeds.feedburner.com/ogfeed", "Only Gizmos ");
        feedNames.put("http://trak.in/feed/", "Trak.in ");
    }


    private Typeface typeface = null;
    private static AppCompatActivity activity = null;
    private ArrayList<RssItem> rssItems;

    private static ItemClickListener itemClickListener = null;
    private static final String TAG = "Nadget";

    private static HashMap<String,Integer> feedDrawables = null;
    private static HashMap<String,String> feedNames = null;

    //private ArrayList<RssItem> updatedItems = new ArrayList<RssItem>();

}
