package de.fhg_radolfzell.android_app.event;


import de.fhg_radolfzell.android_app.data.Post;

/**
 * @author Jannik
 */
public class JSONFeedLoadedEvent {

    public Post[] feed;

    public JSONFeedLoadedEvent(Post[] feed) {
        this.feed = feed;
    }
}
