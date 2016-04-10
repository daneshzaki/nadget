package in.pleb.nadget;
import android.util.Log;

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
            //Log.i("Nadget RssItem in",pubDate);
            //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, DD MMM yyyy HH:mm:ss");
            //Date pubDateDt = dateFormat.parse(pubDate);
            //pubDate = dateFormat.format(pubDateDt);
            this.pubDate = pubDate;
            //Log.i("Nadget RssItem out",pubDate);

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



}