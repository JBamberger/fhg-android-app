package de.jbamberger.api.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class FeedChunk {

    @NonNull
    private String updated;

    @NonNull
    private List<FeedItem> items;

    public FeedChunk(@NonNull String updated, @NonNull List<FeedItem> items) {
        this.updated = updated;
        this.items = items;
    }

    @NonNull
    public String getUpdated() {
        return updated;
    }

    @NonNull
    public List<FeedItem> getItems() {
        return items;
    }
}
