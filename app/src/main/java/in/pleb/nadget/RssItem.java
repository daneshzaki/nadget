package in.pleb.nadget;
import android.util.Log;
import org.ocpsoft.prettytime.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RssItem
{
    public String getDescription() {return description;}

    public String getImageUrl() { return imageUrl; }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        try
        {
            Log.i(TAG, "date in="+pubDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            Date pubDateDt = dateFormat.parse(pubDate);
            //pubDate = dateFormat.format(pubDateDt);
            PrettyTime prettyTime = new PrettyTime();
            //pubDate = prettyTime.format(pubDateDt);
            pubDate = prettyTime.formatDuration(pubDateDt) +" ago";
            this.pubDate = pubDate;
            Log.i(TAG, "date out="+pubDate);

        }
        catch(java.text.ParseException pe)
        {
            Log.e(TAG,"Different date format!!! "+pe.toString());

            //sometimes another format
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE ,dd MMM yyyy HH:mm:ss Z");
                Date pubDateDt = dateFormat.parse(pubDate);
                PrettyTime prettyTime = new PrettyTime();
                pubDate = prettyTime.formatDuration(pubDateDt) +" ago";
                this.pubDate = pubDate;
            }
            //beyond that format just display the input
            catch(java.text.ParseException peinpe)
            {
                peinpe.toString();
                this.pubDate = pubDate;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e("RssItem", e.toString());
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //set the feed source
    public void setSource(String source)
    {
        this.source = source;
    }

    public String getSource()
    {
        return source;
    }



    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("title=");
        buffer.append(title);
        buffer.append(" description=");
        buffer.append(description);
        buffer.append(" link=");
        buffer.append(link);
        buffer.append(" imageUrl=");
        buffer.append(imageUrl);
        buffer.append(" pubDate=");
        buffer.append(pubDate);

        return buffer.toString();
    }

    private String title;
    private String description;
    private String link;
    private String imageUrl;
    private String pubDate;
    private String source;
    private static final String TAG = "Nadget RssItem";



}
