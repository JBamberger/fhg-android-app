package de.fhg_radolfzell.android_app.main.feed;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;
import de.fhg_radolfzell.android_app.event.FeedLoadingFailedEvent;
import de.fhg_radolfzell.android_app.event.JSONFeedLoadedEvent;
import de.fhg_radolfzell.android_app.data.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class FeedInteractorImpl implements FeedInteractor {

    private static final String TAG = "FeedInteractorImpl";

    private final Bus eventBus;
    private final FhgWebInterface api;

    @Inject
    public FeedInteractorImpl(Bus eventBus, FhgWebInterface api) {
        this.eventBus = eventBus;
        this.api = api;
    }


    /**
     * Load the FHG-Feed and deliver the result on the event bus. If parsing of the first Feed fails
     * the second feed will be tried. If this one fails too, the User is notified.
     */
    @Override
    public void getFeed() {
        api.getFeed().enqueue(new Callback<Post[]>() {
            @Override
            public void onResponse(Call<Post[]> call, Response<Post[]> response) {
                if (response.isSuccessful()) {
                    eventBus.post(new JSONFeedLoadedEvent(response.body()));
                } else {
                    failedLoading();
                }
            }

            @Override
            public void onFailure(Call<Post[]> call, Throwable t) {
                Timber.e(t, "onFailure: JSON Feed loading failed");
                failedLoading();

            }
        });
        /*api.getAtomFeed().enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                if (response.isSuccessful()) {
                    eventBus.post(new AtomFeedLoadedEvent(response.body()));
                } else {
                    failedLoading();
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Atom Feed loading failed", t);
                api.getXMLFeed().enqueue(new Callback<RSS>() {
                    @Override
                    public void onResponse(Call<RSS> call, Response<RSS> response) {
                        if (response.isSuccessful()) {
                            eventBus.post(new RSSFeedLoadedEvent(response.body()));
                        } else {
                            failedLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<RSS> call, Throwable t) {
                        Log.e(TAG, "onFailure: RSS feed loading failed", t);
                        failedLoading();
                    }
                });
            }
        });*/


    }

    private void failedLoading() {
        eventBus.post(new FeedLoadingFailedEvent());
    }
}
