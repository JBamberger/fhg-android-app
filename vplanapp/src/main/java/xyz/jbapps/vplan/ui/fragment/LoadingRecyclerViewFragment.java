package xyz.jbapps.vplan.ui.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;

public class LoadingRecyclerViewFragment extends BaseFragment {

    private static final String TAG = "LoadingRecyclerViewFragment";

    protected FloatingActionButton floatingActionButton;
    protected ProgressBar progressBar;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loading_recyclerview, container, false);


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

    protected boolean wifiConnected = false;
    protected boolean mobileConnected = false;

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

}

