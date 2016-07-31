package xyz.jbapps.vplan.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.FHGFeed;
import xyz.jbapps.vplan.ui.FHGFeedAdapter;
import xyz.jbapps.vplan.util.network.FHGFeedRequest;

@Deprecated
public class FeedFragment extends BaseFragment {

    private static final String TAG = "FeedFragment";

    protected FloatingActionButton floatingActionButton;
    protected ProgressBar progressBar;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected Context context;
    protected boolean wifiConnected = false;
    protected boolean mobileConnected = false;
    private static final String URL_FHG_FEED = "http://www.fhg-radolfzell.de/feed/atom";
    private static final String TAG_FHG_FEED = "FHGFeed";
    private static final String FEED_URL = "http://fhg-radolfzell.de/wp-json/wp/v2/posts";

    private RequestQueue netQueue;
    private FHGFeedAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        netQueue = Volley.newRequestQueue(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        floatingActionButton = ViewUtils.findViewById(view, R.id.floatingActionButton);
        progressBar = ViewUtils.findViewById(view, R.id.progressBar);
        recyclerView = ViewUtils.findViewById(view, R.id.recyclerView);
        swipeRefreshLayout = ViewUtils.findViewById(view, R.id.swipeRefreshLayout);
        floatingActionButton.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_rotate_animation));
        swipeRefreshLayout.setColorSchemeResources(R.color.brand_accent_light, R.color.brand_accent_dark, R.color.brand_accent_light, R.color.brand_accent_dark);
        recyclerView.hasFixedSize();
        return view;
    }

    protected void toggleLoading(boolean on) {
        if (on) {
            progressBar.setVisibility(View.VISIBLE);
            floatingActionButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_rotate_animation));
        } else {
            progressBar.setVisibility(View.GONE);
            floatingActionButton.clearAnimation();
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    protected void updateNetworkFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_fhg_feed);

        Activity activity = getActivity();
        if (activity != null) {
            int orientation = activity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

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