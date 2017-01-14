package xyz.jbapps.vplan.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.FHGFeed;
import xyz.jbapps.vplan.ui.FHGFeedAdapter;
import xyz.jbapps.vplan.util.network.FHGFeedRequest;

public class FHGFeedFragment extends LoadingRecyclerViewFragment{

    private static final String TAG = "FHGFeedFragment";
    private static final String URL_FHG_FEED = "https://www.fhg-radolfzell.de/feed/atom";
    private static final String TAG_FHG_FEED = "FHGFeed";

    private RequestQueue netQueue;
    private FHGFeedAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            loadFeed();
    }

    public void loadFeed() {
        toggleLoading(true);
        netQueue.cancelAll(TAG_FHG_FEED);
        FHGFeedRequest req = new FHGFeedRequest(URL_FHG_FEED, new Response.Listener<FHGFeed>() {
            @Override
            public void onResponse(FHGFeed response) {
                toggleLoading(false);
                adapter = new FHGFeedAdapter(getActivity());
                adapter.setData(response.feedItems);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                toggleLoading(false);
                Toast.makeText(context, getString(R.string.text_loading_failed), Toast.LENGTH_LONG).show();
            }
        });
        req.setTag(TAG_FHG_FEED);
        netQueue.add(req);
    }
}
