package de.jbamberger.vplan.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.jbamberger.vplan.util.FHGFeedXmlParser;

public class FHGFeed {
    @SerializedName("http_last_updated")
    public long lastUpdated = 0;
    @SerializedName("fhg_feed")
    public List<FHGFeedXmlParser.FHGFeedItem> feedItems = new ArrayList<>();
}
