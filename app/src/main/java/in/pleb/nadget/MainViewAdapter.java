package in.pleb.nadget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;

/**
 * Created by danesh on 03-04-2016.
 */
public class MainViewAdapter  extends RecyclerView.Adapter<MainViewAdapter.PostViewHolder>
{
    public MainViewAdapter(Activity activity, ArrayList<RssItem> rssItems)
    {
        this.activity = activity;
        this.rssItems = rssItems;
        Log.i(TAG,"MainViewAdapter rssItems ="+rssItems);

        //set fonts for all text
        typeface = Typeface.createFromAsset( this.activity.getResources().getAssets(), "SourceSansPro-Regular.otf");
    }

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
        holder.postDateView.setText(rssItems.get(position).getPubDate());
        holder.postTitleView.setTypeface(typeface, Typeface.BOLD);
        holder.postDateView.setTypeface(typeface, Typeface.ITALIC);

        //TODO: set post image
        //holder.postImageView.setImageResource();
        //use setScaleType in image view to size images

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
        //Log.i(TAG,"MainViewAdapter getItemCount titleArr len"+rssItems.size());
        return rssItems.size();
    }

    public interface ItemClickListener
    {
        public void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    private Typeface typeface = null;
    private Activity activity = null;
    private ArrayList<RssItem> rssItems = null;

    private static ItemClickListener itemClickListener = null;
    private static final String TAG = "Nadget Main";

}
