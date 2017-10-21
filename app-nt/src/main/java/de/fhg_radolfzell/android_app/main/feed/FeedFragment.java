package de.fhg_radolfzell.android_app.main.feed;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.event.AtomFeedLoadedEvent;
import de.fhg_radolfzell.android_app.event.FeedLoadingFailedEvent;
import de.fhg_radolfzell.android_app.event.JSONFeedLoadedEvent;
import de.fhg_radolfzell.android_app.event.RSSFeedLoadedEvent;
import de.fhg_radolfzell.android_app.view.BaseFragment;
import de.fhg_radolfzell.android_app.main.MainActivity;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class FeedFragment extends BaseFragment {

    private static final String TAG = "FeedFragment";
    @Inject
    FeedAdapter adapter;
    @Inject
    FeedInteractor interactor;
    private FeedComponent feedComponent;

    public FeedComponent getFeedComponent() {
        return feedComponent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedComponent = ((MainActivity) getActivity()).getMainComponent().newFeedComponent(new FeedModule(this));
        feedComponent.inject(this);
    }

    @Override
    public void onDestroy() {
        feedComponent = null;
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setAdapter(adapter);
        update();
    }

    @Subscribe
    public void feedLoaded(RSSFeedLoadedEvent event) {
        Timber.d("Received RSS Feed update.");
        showLoadingIndicator(false);
        adapter.setData(event.feed);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void feedLoaded(AtomFeedLoadedEvent event) {
        Timber.d("Received Atom Feed update.");
        showLoadingIndicator(false);
        adapter.setData(event.feed);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void feedLoaded(JSONFeedLoadedEvent event) {
        Timber.d("Received Atom Feed update.");
        showLoadingIndicator(false);
        adapter.setData(event.feed);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void feedLoadingFailed(FeedLoadingFailedEvent event) {
        Timber.d("Feed update failed.");
        showLoadingIndicator(false);
        Toast.makeText(context, R.string.feed_loading_failed, Toast.LENGTH_LONG).show();
    }


    public void update() {
        Timber.d("Invoked Feed update.");
        showLoadingIndicator(true);
        interactor.getFeed();
    }
}
