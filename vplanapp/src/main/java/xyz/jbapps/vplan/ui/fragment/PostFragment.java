package xyz.jbapps.vplan.ui.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.List;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.PostAdapter;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;
import xyz.jbapps.vplan.util.jsonapi.net.PostRequest;

public class PostFragment extends LoadingRecyclerViewFragment {

    private static final String TAG = "PostFragment";
    private static final String URL_POSTS = "http://fhg-radolfzell.de/wp-json/wp/v2/posts";
    private static final String TAG_POSTS = "PostRequest";

    private RequestQueue netQueue;
    private PostAdapter adapter;

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
                loadPosts();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPosts();
            }
        });

        loadPosts();
    }

    public void loadPosts() {
        toggleLoading(true);
        netQueue.cancelAll(URL_POSTS);
        PostRequest req = new PostRequest(URL_POSTS, new Response.Listener<List<PostItem>>() {
            @Override
            public void onResponse(List<PostItem> posts) {
                toggleLoading(false);
                adapter = new PostAdapter(getActivity());
                adapter.setData(posts);

                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                toggleLoading(false);
                Toast.makeText(context, getString(R.string.text_loading_failed), Toast.LENGTH_LONG).show();
            }
        });
        req.setTag(TAG_POSTS);
        netQueue.add(req);
    }
}
