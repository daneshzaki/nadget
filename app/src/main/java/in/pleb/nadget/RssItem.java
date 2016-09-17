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
            //format for almost all feeds
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            dateFormat.setLenient(true);
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

            //format for some feeds
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE ,dd MMM yyyy HH:mm:ss Z");
                dateFormat.setLenient(true);
                Date pubDateDt = dateFormat.parse(pubDate);
                PrettyTime prettyTime = new PrettyTime();
                pubDate = prettyTime.formatDuration(pubDateDt) +" ago";
                this.pubDate = pubDate;
            }
            //beyond that format just display the input
            catch(java.text.ParseException peinpe)
            {
                //Gizmodo format
                try
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                    dateFormat.setLenient(true);
                    Date pubDateDt = dateFormat.parse(pubDate);
                    PrettyTime prettyTime = new PrettyTime();
                    pubDate = prettyTime.formatDuration(pubDateDt) +" ago";
                    this.pubDate = pubDate;
                }
                catch(java.text.ParseException anotherEx)
                {
                    //Digit format
                    try
                    {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"	);
                        //                                                 "2016-06-24T20:42:33+05:30"
                        dateFormat.setLenient(true);
                        Date pubDateDt = dateFormat.parse(pubDate);
                        PrettyTime prettyTime = new PrettyTime();
                        pubDate = prettyTime.formatDuration(pubDateDt) +" ago";
                        this.pubDate = pubDate;
                    }
                    catch(java.text.ParseException yetAnotherEx) {
                        //News18 format
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm a");
                            //                                                 "Saturday,September 17,2016 12:52 pm"
                            dateFormat.setLenient(true);
                            Date pubDateDt = dateFormat.parse(pubDate);
                            PrettyTime prettyTime = new PrettyTime();
                            pubDate = prettyTime.formatDuration(pubDateDt) + " ago";
                            this.pubDate = pubDate;
                        }
                        //beyond that format just display the input date
                        catch(java.text.ParseException anotherEx2)
                        {
                            Log.e(TAG,"News 18 date format error: "+anotherEx2.toString());
                            this.pubDate = pubDate;
                        }
                    }
                }

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
