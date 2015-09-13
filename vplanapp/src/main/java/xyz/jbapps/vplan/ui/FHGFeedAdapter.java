package xyz.jbapps.vplan.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.jutils.MiscUtils;
import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.FHGFeedXmlParser;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class FHGFeedAdapter extends RecyclerView.Adapter {

    private static final String TAG = "FHGFeedAdapter";

    private List<FHGFeedXmlParser.FHGFeedItem> feedItems;
    private final Activity activity;

    public FHGFeedAdapter(Activity activity) {
        feedItems = new ArrayList<>();
        this.activity = activity;
    }

    public void setData(List<FHGFeedXmlParser.FHGFeedItem> feedItems) {
        this.feedItems = feedItems;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fhg_feed, parent, false);

        return new FeedItemViewHolder(v0, new FeedItemViewHolder.IOnItemClicked() {
            @Override
            public void onItemClicked(View v) {
                String link = (String) v.getTag(R.id.item_card);
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
        FHGFeedXmlParser.FHGFeedItem row = feedItems.get(position);
        FeedItemViewHolder rowHolder = (FeedItemViewHolder) holder;
        rowHolder.title.setText(row.title);
        rowHolder.meta.setText("Von " + row.author + " am " + row.published_at);
        rowHolder.summary.setText(row.summary);
        rowHolder.cardView.setTag(R.id.item_card, row.link);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public static class FeedItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final CardView cardView;
        public final TextView title;
        public final TextView meta;
        public final TextView summary;
        private final IOnItemClicked listner;

        public FeedItemViewHolder(View itemView, IOnItemClicked listener) {
            super(itemView);
            this.listner = listener;
            cardView = ViewUtils.findViewById(itemView, R.id.item_card);
            title = ViewUtils.findViewById(itemView, R.id.item_title);
            meta = ViewUtils.findViewById(itemView, R.id.item_meta);
            summary = ViewUtils.findViewById(itemView, R.id.item_summary);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listner != null) {
                listner.onItemClicked(v);
            }
        }

        interface IOnItemClicked {
            void onItemClicked(View v);
        }
    }
}
