package in.pleb.nadget;

/**
 * Created by danesh on 24-03-2016.
 */

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ExtendedRssParser extends DefaultHandler
{

    //Constructor.
    public ExtendedRssParser(String url,int maximumResults){
        super();
        this.url=url;
        this.maximumResults=maximumResults;
    }


    /**
     Returns an HTML representation of the news feed being
     parsed.
     */
    public synchronized void parse() throws IOException, ParserConfigurationException
    {
        Log.i(TAG, "ExtendedRssParser parse entry maxres"+maximumResults);
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xr = saxParser.getXMLReader();
            xr.setContentHandler(this);
            xr.setErrorHandler(this);
            URL u=new URL(url);
            URLConnection UC=u.openConnection();
            //for instrumentation
            long entryTime = System.currentTimeMillis();
            Log.i(TAG, "ExtendedRssParser parse entry time="+entryTime);
            //feeds not returned if user agent not set
            UC.setRequestProperty ( "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            Log.i(TAG, "ExtendedRssParser parse set agent done");
            InputStreamReader r = new InputStreamReader(UC.getInputStream());
            Log.i(TAG, "ExtendedRssParser calling super parse ");
            xr.parse(new InputSource(r));
            long exitTime = System.currentTimeMillis();
            long timeTaken = exitTime - entryTime;

            Log.i(TAG, "ExtendedRssParser parse exit time="+timeTaken);
        }
        catch(SAXException e)
        {
            Log.i(TAG, "ExtendedRssParser parse "+e.toString());
        }

        //Log.i(TAG, "ExtendedRssParser list="+rssItemList);

    }

    // Called when the XML file begins.
    public void startDocument ()
    {
    }


    //Called when the end of the XML file is reached.
    public void endDocument ()
    {
                /*If we have a partially parsed news item throw it
                  into our array.*/
        if(currentItem!=null){
            rcount++;
            rssItemList.add(currentItem);
        }
    }


    //Called when we begin parsing the XML file.
    public void startElement (String uri, String name,
                              String qName, Attributes atts) throws SAXException
    {
        /*if(isLimitReached)
        {
            return;
        }*/
        //qName contains the non-URI name of the XML element.
        if(qName.equals("item")){
            if(currentItem!=null){
                //We've fetched another news item.
                rcount++;
                //Add it to our ArrayList.
                rssItemList.add(currentItem);

                //Maximum results have been reached.
                if(rcount==maximumResults)
                {
                    throw new SAXException("\nLimit reached.");
                    //isLimitReached = true;
                    //return;
                }
            }
            //Create a new NewsItem to add data to.
            currentItem=new RssItem();
            currentItem.setSource(url);
        }
    }


    //We've reached the end of an XML element.
    public void endElement (String uri, String name, String qName) throws SAXException
    {
        /*if(isLimitReached)
        {
            return;
        }*/

        if(qName.equals("title")&&currentItem!=null){//Are we parsing a news item?
            currentItem.setTitle(currentText.trim());
        }
        else if(qName.equals("link")&&currentItem!=null)
        {
            currentItem.setLink(currentText);
            //get images for feeds
            ImageUrlParser imageUrlParser = new ImageUrlParser();
            //imageUrlParser.setAdapter(adapter);
            imageUrlParser.execute(currentItem);
            //imageUrlParser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentItem);

        }
        else if(qName.equals("pubDate")&&currentItem!=null)
            currentItem.setPubDate(currentText.trim());
        else if(qName.equals("description")&&currentItem!=null)
            currentItem.setDescription(currentText.trim());

        //image url setting problem
        else if (qName.equals("media:thumbnail") || qName.equals("media:content") || qName.equals("image"))
        {
            Log.i(TAG, "ExtendedRssParser checking image url "+currentItem);
            if(currentItem != null)
            {
                currentItem.setImageUrl(currentText);
                Log.i(TAG, "ExtendedRssParser setting image url"+currentText);
            }
        }
        
        //Make sure we don't attempt to parse too long of a document.
        currentText="";
         ecount++;

        if(ecount>MAX_ELEMENTS) throw new SAXException("\nLimit reached");
        /*if(ecount>MAX_ELEMENTS)
        {
            isLimitReached = true;
            return;
        }*/
    }

    //Parse characters from the current element we're parsing.
    public void characters (char ch[], int start, int length)
    {
        for(int i=start;i<start+length;i++){
            currentText+=ch[i];
        }
    }

    //return the news feed
    public List<RssItem> getItems()
    {
        return rssItemList;
    }


    //How many RSS news items should we load before stopping.
    private int maximumResults = 20;
    /*How many elements should we allow before stopping the parse
      this stops giant files from breaking the server.*/
    private static final int MAX_ELEMENTS = 500;
    private boolean isLimitReached = false;
    //Keep track of the current element count.
    private int ecount=0;
    //Keep track of the current news item count.
    private int rcount=0;
    private String url="";//Url to parse.
    //String to store parsed data to.
    private String output="<i>Error parsing RSS feed.</i>";
    //Current string being parsed.
    private String currentText="";
    //Current RSS rssItemList Item.
    private RssItem currentItem=null;
    //ArrayList of all current rssItemList Items.
    private ArrayList rssItemList=new ArrayList();

    private final String TAG = "Nadget";
}