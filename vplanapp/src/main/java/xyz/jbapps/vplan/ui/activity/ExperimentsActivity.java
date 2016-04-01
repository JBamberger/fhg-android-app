package xyz.jbapps.vplan.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.List;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;
import xyz.jbapps.vplan.util.jsonapi.net.PostRequest;

public class ExperimentsActivity extends AppCompatActivity {

    RequestQueue netQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        netQueue = Volley.newRequestQueue(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hsjldfhjsakdfksdhfsdklfhklsdf------------------------------");
                Log.d("", "loading---");
                loadPostList();
            }
        });
    }

    private void loadPostList() {

        PostRequest PostRequest = new PostRequest("http://fhg-radolfzell.de/wp-json/wp/v2/posts", new Response.Listener<List<PostItem>>() {
            @Override
            public void onResponse(List<PostItem> data) {
                System.out.println("finished");
                for (PostItem i : data) {
                    System.out.println(i.id
                    + " : " + i.date
                    + " : " + i.date_gmt
                    + " : " + i.modified
                    + " : " + i.modified_gmt
                    + " : " + i.slug
                    + " : " + i.link
                    + " : " + i.title
                    + " : " + i.content
                    + " : " + i.excerpt
                    + " : " + i.author
                    + " : " + i.featured_media
                    + " : " + i.comment_status
                    + " : " + i.ping_status
                    + " : " + i.sticky
                    + " : " + i.format
                    );
                    for (String c : i.categories) {
                        System.out.println("categories: " + c);
                    }

                    for (String t : i.tags) {
                        System.out.println("tag: " + t);
                    }
                }
            }
        },errorListener);
        PostRequest.setTag("medreq");

        netQueue.add(PostRequest);
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), getString(R.string.text_loading_failed), Toast.LENGTH_LONG).show();
            System.out.println(error.getMessage());
            error.printStackTrace();
        }
    };

}
