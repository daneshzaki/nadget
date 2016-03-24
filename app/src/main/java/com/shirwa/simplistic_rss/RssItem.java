package com.shirwa.simplistic_rss;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
/*
 * Copyright (C) 2014 Shirwa Mohamed <shirwa99@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RssItem {
    String title;
    String description;
    String link;
    String imageUrl;
    String pubDate;

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
}
