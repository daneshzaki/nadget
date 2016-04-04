package in.pleb.nadget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Created by danesh on 03-04-2016.
 */
public class MainViewAdapter extends BaseAdapter
{
    public MainViewAdapter(Activity activity, int resource, ArrayList<String> titleList, ArrayList<String> pubDateList, ArrayList<String> imageLinkList)
    {
        this.activity = activity;

        titleArr = titleList.toArray(new String[titleList.size()]);

        pubDateArr = pubDateList.toArray(new String[titleList.size()]);
        imageLinkArr = imageLinkList.toArray(new String[titleList.size()]);

        //set fonts for all text
        typeface = Typeface.createFromAsset( this.activity.getResources().getAssets(), "SourceSansPro-Regular.otf");
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return titleArr.length;
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    //construct a row
    public class Holder
    {
        ImageView postImageView;
        TextView postTitleView, postDateView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Log.i(TAG, "onActivityCreated getView starting...");
        Holder holder = new Holder();

        View rowView;

        rowView = inflater.inflate(R.layout.main_view_row, null);

        holder.postTitleView=(TextView) rowView.findViewById(R.id.postTitle);
        holder.postDateView=(TextView) rowView.findViewById(R.id.postDate);
        holder.postImageView=(ImageView) rowView.findViewById(R.id.postImage);

        holder.postTitleView.setText(titleArr[position]);
        holder.postDateView.setText(pubDateArr[position]);
        holder.postTitleView.setTypeface(typeface, Typeface.BOLD);
        holder.postDateView.setTypeface(typeface, Typeface.ITALIC);

        //TODO: set below attrib in XML
        //TODO: set post image
        //holder.postImageView.setImageResource();
        //use setScaleType in image view to size images
        return rowView;
    }



    private Typeface typeface = null;
    private Activity activity = null;
    private String[] titleArr = null;
    private String[] pubDateArr = null;
    private String[] imageLinkArr = null;
    private LayoutInflater inflater = null;

}
