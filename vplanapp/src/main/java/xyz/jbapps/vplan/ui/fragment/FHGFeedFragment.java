package xyz.jbapps.vplan.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.FHGFeedAdapter;
import xyz.jbapps.vplan.util.FHGFeedProvider;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;

public class FHGFeedFragment extends LoadingRecyclerViewFragment implements FHGFeedProvider.IFHGFeedResultListener {

    private FHGFeedProvider feedProvider;
    private static final String STATE_SHOULD_REFRESH = "refresh_feed";
    private static final String TAG = "FHGFeedFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_fhg_feed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeed();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeed();
            }
        });
        return v;
    }

    private boolean stateShouldRefresh = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateShouldRefresh = (savedInstanceState == null) || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (stateShouldRefresh) {
            loadFeed();
        } else {
            loadCachedFeed();
        }
    }

    private void loadCachedFeed() {
        toggleLoading(true);
        if (feedProvider != null) {
            feedProvider.cancel(true);
            feedProvider = null;
        }
        feedProvider = new FHGFeedProvider(getActivity(), FHGFeedProvider.TYPE_CACHE, this);
        feedProvider.execute();
        Log.d(TAG, "Cache loading");
    }

    private void loadFeed() {
        updateNetworkFlags();
        if (!wifiConnected && !mobileConnected) {
            loadCachedFeed();
            Toast.makeText(getActivity(), R.string.text_network_disconnected, Toast.LENGTH_LONG).show();
            return;
        }
        toggleLoading(true);
        if (feedProvider != null) {
            feedProvider.cancel(true);
            feedProvider = null;
        }
        feedProvider = new FHGFeedProvider(getActivity(), FHGFeedProvider.TYPE_LOAD, this);
        feedProvider.execute();
        Log.d(TAG, "loading Feed");
    }

    @Override
    public void feedLoadingFailed() {
        toggleLoading(false);
        Toast.makeText(context, "Fehler beim laden", Toast.LENGTH_LONG).show(); //TODO res
    }

    @Override
    public void feedLoadingSucceeded(List<FHGFeedXmlParser.FHGFeedItem> feed) {
        toggleLoading(false);
        adapter = new FHGFeedAdapter(getActivity());
        adapter.setData(feed);
        recyclerView.setAdapter(adapter);
    }

    FHGFeedAdapter adapter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        boolean re = isListEmpty();
        outState.putBoolean(STATE_SHOULD_REFRESH, re);
    }

    private boolean isListEmpty() {
        return adapter.getItemCount() == 0;
    }

}
