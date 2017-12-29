package de.jbamberger.api.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class FeedChunk {

    @NonNull
    private List<FeedItem> items;

    public FeedChunk(@NonNull List<FeedItem> items) {
        this.items = items;
    }

    @NonNull
    public List<FeedItem> getItems() {
        return items;
    }
}
