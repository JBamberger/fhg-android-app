package de.fhg_radolfzell.android_app.main.feed;

import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;
import de.fhg_radolfzell.android_app.main.MainActivity;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
public class FeedModule {

    private FeedFragment feedFragment;

    public FeedModule(FeedFragment feedFragment) {

        this.feedFragment = feedFragment;
    }

    @Provides
    @FeedScope
    public FeedFragment providesFeedFragment() {
        return feedFragment;
    }

    @Provides
    @FeedScope
    public FeedAdapter providesFeedAdapter(MainActivity mainActivity) {
        return new FeedAdapter(mainActivity);
    }

    @Provides
    @FeedScope
    public FeedInteractor providesFeedInteractor(Bus bus, FhgWebInterface api) {
        return new FeedInteractorImpl(bus, api);
    }

}
