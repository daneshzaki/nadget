package in.pleb.nadget;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Given a url to a web page, extract a suitable image from that page. This will
 * attempt to follow a method similar to Google+, as described <a href=
 * "http://webmasters.stackexchange.com/questions/25581/how-does-google-plus-select-an-image-from-a-shared-link"
 * >here</a>
 *
 * Class courtesy: Gavin Bisesi (Daenyth) https://github.com/Daenyth
 * 
 */
public class ImageExtractor {

    public static String extractImageUrl(String url) throws IOException {

        String contentType = new URL(url).openConnection().getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return url;
            }
        }

        Document document = Jsoup.connect(url).get();

        String imageUrl = null;

        imageUrl = getImageFromSchema(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromOpenGraph(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromTwitterCard(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromTwitterShared(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromLinkRel(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromGuess(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        imageUrl = getImageFromBlog(document);
        Log.i(TAG, "ImageExtractor imageUrl = "+imageUrl);
        if (imageUrl != null) {
            return imageUrl;
        }

        return imageUrl;
    }

    private static String getImageFromTwitterShared(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromTwitterShared ");

        Element div = document.select("div.media-gallery-image-wrapper").first();
        if (div == null) {
            return null;
        }
        Element img = div.select("img.media-slideshow-image").first();
        if (img != null) {
            return img.absUrl("src");
        }
        return null;
    }

    private static String getImageFromGuess(Document document) {
        // not implemented
        Log.i(TAG, "ImageExtractor getImageFromGuess ");
        return null;
    }

    private static String getImageFromLinkRel(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromLinkRel ");
        Element link = document.select("link[rel=image_src]").first();
        if (link != null) {
            return link.attr("abs:href");
        }
        return null;
    }

    private static String getImageFromTwitterCard(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromTwitterCard ");

        Element meta = document.select("meta[name=twitter:card][content=photo]").first();
        if (meta == null) {
            return null;
        }
        Element image = document.select("meta[name=twitter:image]").first();
        return image.attr("abs:content");
    }

    private static String getImageFromOpenGraph(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromOpenGraph ");

        Element image = document.select("meta[property=og:image]").first();
        if (image != null) {
            return image.attr("abs:content");
        }
        Element secureImage = document.select("meta[property=og:image:secure]").first();
        if (secureImage != null) {
            return secureImage.attr("abs:content");
        }
        return null;
    }

    private static String getImageFromSchema(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromSchema ");

        Element container =
            document.select("*[itemscope][itemtype=http://schema.org/ImageObject]").first();
        if (container == null) {
            return null;
        }

        Element image = container.select("img[itemprop=contentUrl]").first();
        if (image == null) {
            return null;
        }
        return image.absUrl("src");
    }

    //added by Danesh 8/7/16
    private static String getImageFromBlog(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromBlog ");

        /*Element container =
                document.select("div.entry-content img").first();
        Log.i(TAG, "ImageExtractor getImageFromBlog container = "+container);
        if (container == null) {
            return null;
        }*/

        //Element image = container.select("img[itemprop=contentUrl]").first();
        Element image = document.select("div.entry-content img").first();
        Log.i(TAG, "ImageExtractor getImageFromBlog image = "+image);

        if (image == null) {
            return null;
        }
        return image.absUrl("src");
    }

    //added by Danesh 8/7/16 for TOI
    private static String getImageFromSite(Document document) {
        Log.i(TAG, "ImageExtractor getImageFromSite document = "+document.html());

        /*Element container =
                document.select("div.entry-content img").first();
        Log.i(TAG, "ImageExtractor getImageFromBlog container = "+container);
        if (container == null) {
            return null;
        }*/

        String selector ="[property= ~og:image]";
        Elements image = document.select(selector);
        Log.i(TAG, "ImageExtractor getImageFromSite images size = "+image.size());

        if (image == null) {
            return null;
        }
        //Log.i(TAG, "ImageExtractor getImageFromSite image = "+image.select("[href]"));
        return image.text();
    }

    private static final String TAG = "Nadget";
}