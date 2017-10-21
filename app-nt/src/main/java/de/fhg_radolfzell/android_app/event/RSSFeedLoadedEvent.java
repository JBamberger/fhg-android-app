package de.fhg_radolfzell.android_app.event;


import de.fhg_radolfzell.android_app.data.RSS;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class RSSFeedLoadedEvent {

    public RSS feed;

    public RSSFeedLoadedEvent(RSS feed) {
        this.feed = feed;
    }
}
