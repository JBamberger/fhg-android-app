package de.fhg_radolfzell.android_app.main.feed;

import dagger.Subcomponent;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@FeedScope
@Subcomponent(
        modules = {
                FeedModule.class
        }
)
public interface FeedComponent {

    void inject(FeedFragment fragment);

    void inject(FeedInteractorImpl interactor);
}
