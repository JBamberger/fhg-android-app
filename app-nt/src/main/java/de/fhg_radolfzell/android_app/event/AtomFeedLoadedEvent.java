package de.fhg_radolfzell.android_app.event;


import de.fhg_radolfzell.android_app.data.Feed;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class AtomFeedLoadedEvent {

    public Feed feed;

    public AtomFeedLoadedEvent(Feed feed) {
        this.feed = feed;
    }
}
