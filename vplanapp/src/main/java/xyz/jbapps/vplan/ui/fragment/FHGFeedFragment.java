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

    private static final String TAG = "FHGFeedFragment";
    private static final String STATE_SHOULD_REFRESH = "refresh_feed";

    private FHGFeedProvider feedProvider;
    private FHGFeedAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_fhg_feed);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeed(FHGFeedProvider.TYPE_LOAD);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeed(FHGFeedProvider.TYPE_LOAD);
            }
        });

        if ((savedInstanceState == null) || savedInstanceState.getBoolean(STATE_SHOULD_REFRESH, true)) {
            loadFeed(FHGFeedProvider.TYPE_LOAD);
        } else {
            loadFeed(FHGFeedProvider.TYPE_CACHE);
        }
    }

    public void loadFeed(int method) {
        updateNetworkFlags();
        if (!wifiConnected && !mobileConnected) {
            toggleLoading(false);
            Toast.makeText(context, R.string.text_network_disconnected, Toast.LENGTH_LONG).show();
            return;
        }
        if (method == FHGFeedProvider.TYPE_LOAD && wifiConnected) {
            method = FHGFeedProvider.TYPE_FORCE_LOAD;
        }
        toggleLoading(true);
        if (feedProvider != null) {
            feedProvider.cancel(true);
            feedProvider = null;
        }
        feedProvider = new FHGFeedProvider(context, method, this);
        feedProvider.execute();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SHOULD_REFRESH, adapter != null && adapter.getItemCount() == 0);
    }
}
