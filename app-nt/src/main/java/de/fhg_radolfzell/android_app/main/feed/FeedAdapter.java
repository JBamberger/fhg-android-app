package de.fhg_radolfzell.android_app.main.feed;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.databinding.FeedFooterBinding;
import de.fhg_radolfzell.android_app.databinding.FeedHeaderBinding;
import de.fhg_radolfzell.android_app.databinding.FeedItemBinding;
import de.fhg_radolfzell.android_app.data.Feed;
import de.fhg_radolfzell.android_app.data.Post;
import de.fhg_radolfzell.android_app.data.RSS;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class FeedAdapter extends RecyclerView.Adapter {

    private static final String TAG = "FeedAdapter";
    private FeedDataWrapperImpl feedDataWrapperImpl;
    private Activity activity;

    public FeedAdapter(Activity activity) {
        this.activity = activity;
        feedDataWrapperImpl = new FeedDataWrapperImpl();
    }

    public void setData(RSS feed) {
        feedDataWrapperImpl.setData(feed);
        notifyDataSetChanged();
    }

    public void setData(Feed feed) {
        feedDataWrapperImpl.setData(feed);
        notifyDataSetChanged();
    }

    public void setData(Post[] feed) {
        feedDataWrapperImpl.setData(feed);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType) {
            case FeedDataWrapperImpl.TYPE_HEADER:
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.feed_header, parent, false);
                break;
            case FeedDataWrapperImpl.TYPE_ROW:
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.feed_item, parent, false);
                break;
            case FeedDataWrapperImpl.TYPE_FOOTER:
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.feed_footer, parent, false);
                break;
            default:
                return null;
        }
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        switch (feedDataWrapperImpl.getItemTypeAtPosition(position)) {
            case FeedDataWrapperImpl.TYPE_HEADER:
                FeedHeaderBinding binding = ((FeedHeaderBinding) viewHolder.binding);
                FeedDataWrapperImpl.FeedHeader header = (FeedDataWrapperImpl.FeedHeader) feedDataWrapperImpl.getItemAtPosition(position);
                binding.setHeader(header);
                try {
                    Glide.with(activity)
                            .load(header.icon)
                            .transform(new CircleTransform(activity))
                            .crossFade()
                            .into(binding.feedHeaderIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case FeedDataWrapperImpl.TYPE_ROW:
                FeedItemBinding iBinding = (FeedItemBinding) viewHolder.binding;
                iBinding.setItem((FeedDataWrapperImpl.FeedItem) feedDataWrapperImpl.getItemAtPosition(position));
                iBinding.setListener(this);
                break;
            case FeedDataWrapperImpl.TYPE_FOOTER:
                ((FeedFooterBinding) ((ViewHolder) holder).binding).setFooter((FeedDataWrapperImpl.FeedFooter) feedDataWrapperImpl.getItemAtPosition(position));
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return feedDataWrapperImpl.getItemTypeAtPosition(position);
    }

    public void onClick(String link) {
        Timber.d("onClick() called with: link = [%s]", link);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.setToolbarColor(activity.getResources().getColor(R.color.brand_primary)).build();
        customTabsIntent.launchUrl(activity, Uri.parse(link));
    }

    @Override
    public int getItemCount() {
        return feedDataWrapperImpl.length();
    }

    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ViewDataBinding binding;


        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
