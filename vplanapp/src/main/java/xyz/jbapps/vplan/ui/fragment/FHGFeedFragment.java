package xyz.jbapps.vplan.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFeed();
    }

    private void loadFeed() {
        updateNetworkFlags();
        if (!wifiConnected && !mobileConnected) {
            toggleLoading(false);
            Toast.makeText(getActivity(), R.string.text_network_disconnected, Toast.LENGTH_LONG).show();
            return;
        }
        toggleLoading(true);
        if (feedProvider != null) {
            feedProvider.cancel(true);
            feedProvider = null;
        }
        feedProvider = new FHGFeedProvider(this);
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
        FHGFeedAdapter adapter = new FHGFeedAdapter(getActivity());
        adapter.setData(feed);
        recyclerView.setAdapter(adapter);
    }

}
