package xyz.jbapps.vplan.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.jutils.MiscUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.activity.PostDetailActivity;
import xyz.jbapps.vplan.util.jsonapi.data.PostItem;

public class PostAdapter extends RecyclerView.Adapter {

    private static final String TAG = "PostAdapter";

    private List<PostItem> posts;
    private final Activity activity;

    public PostAdapter(Activity activity) {
        posts = new ArrayList<>();
        this.activity = activity;
    }

    public void setData(List<PostItem> postItems) {
        this.posts = postItems;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fhg_feed, parent, false);

        return new PostItemViewHolder(v0, new PostItemViewHolder.IOnItemClicked() {
            @Override
            public void onItemClicked(View v) {
//                    TODO: fix/implement detail screen
                    /*Intent i = new Intent(activity.getApplicationContext(), PostDetailActivity.class);
                    Bundle b = ((PostItem) v.getTag(R.id.item_card)).toBundle();
                    i.putExtra(PostDetailActivity.BUNDLE_ITEM, b);
                    activity.startActivity(i);*/

                String link = ((PostItem) v.getTag(R.id.item_card)).link;
                Log.d(TAG, link);
                if (MiscUtils.isValidURL(link)) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                } else {
                    Toast.makeText(activity, "Der hinterlegte Link funktioniert nicht", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resources res = activity.getResources();
        PostItem row = posts.get(position);
        PostItemViewHolder rowHolder = (PostItemViewHolder) holder;
        rowHolder.title.setText(row.title);
        rowHolder.meta.setText(String.format(res.getString(R.string.post_info), row.author, row.date));
        rowHolder.summary.setText(Html.fromHtml(row.excerpt));
        rowHolder.cardView.setTag(R.id.item_card, row);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View cardView;
        public final TextView title;
        public final TextView meta;
        public final TextView summary;
        private final IOnItemClicked listener;

        public PostItemViewHolder(View itemView, IOnItemClicked listener) {
            super(itemView);
            this.listener = listener;
            cardView = ViewUtils.findViewById(itemView, R.id.item_card);
            title = ViewUtils.findViewById(itemView, R.id.item_title);
            meta = ViewUtils.findViewById(itemView, R.id.item_meta);
            summary = ViewUtils.findViewById(itemView, R.id.item_summary);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(v);
            }
        }

        interface IOnItemClicked {
            void onItemClicked(View v);
        }
    }
}
