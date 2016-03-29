package in.pleb.nadget;

import android.app.Activity;
import android.os.Bundle;

public class FeedSelector extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_selector);
    }

    //get feed selected

    //return feed selected

    //list of feeds
    // todo move to values xml
    private static final String NDTV_NEWS_FEED = "http://gadgets.ndtv.com/rss/news";
    private static final String BGR_FEED = "http://www.bgr.in/feed/";
    private static final String TIMES_FEED = "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss";
    private static final String TECHTREE_FEED = "http://www.techtree.com/rss.xml";

    private static final String[] feedList = new String[]{
            "http://gadgets.ndtv.com/rss/news",
            "http://www.bgr.in/feed/",
            "http://timesofindia.feedsportal.com/c/33039/f/533923/index.rss",
            "http://www.techtree.com/rss.xml",
            "http://www.firstpost.com/tech/feed",
            "http://indianexpress.com/section/technology/feed/",
            "http://www.gizmodo.in/rss_section_feeds/23005095.cms",
            "http://www.thehindu.com/sci-tech/?service=rss",
            "http://www.digit.in/rss-feed/",
            "http://www.ibtimes.co.in/rss",
            "http://feeds.feedburner.com/igyaan",
            "http://feeds.feedburner.com/Thegeekybyte",
            "http://feeds2.feedburner.com/fone-arena",
            "http://feeds.feedburner.com/ogfeed",
            "http://feeds.feedblitz.com/gogi-technology"
    };

}
