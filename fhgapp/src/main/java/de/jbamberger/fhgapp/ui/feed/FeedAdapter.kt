package de.jbamberger.fhgapp.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.ui.BindingUtils
import de.jbamberger.fhgapp.util.GlideRequests
import de.jbamberger.fhgapp.util.Utils

internal class FeedAdapter(private val glide: GlideRequests) :
    PagingDataAdapter<Pair<FeedItem, FeedMedia?>, FeedAdapter.FeedItemHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemHolder {
        return FeedItemHolder.create(glide, parent)
    }

    override fun onBindViewHolder(holder: FeedItemHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Pair<FeedItem, FeedMedia?>>() {
            override fun areItemsTheSame(
                oldItem: Pair<FeedItem, FeedMedia?>, newItem: Pair<FeedItem, FeedMedia?>
            ) = oldItem.first.id == newItem.first.id

            override fun areContentsTheSame(
                oldItem: Pair<FeedItem, FeedMedia?>, newItem: Pair<FeedItem, FeedMedia?>
            ) = oldItem == newItem
        }
    }

    class FeedItemHolder(
        private val glide: GlideRequests, view: View
    ) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.title)
        private val featuredMedia = view.findViewById<ImageView>(R.id.featured_media)

        private val textLoading = view.context.getString(R.string.feed_status_loading)

        private var post: Pair<FeedItem, FeedMedia?>? = null
        private var link: String? = null

        init {
            view.setOnClickListener {
                this.link?.let { Utils.openUrl(view.context, it) }
            }
        }

        private fun FeedMedia.selectMediaVariant(): FeedMedia.ImageSize? {
            val sizes = this.media_details.sizes
            return try {
                sizes.entries.first { it.key == "thumbnail" }.value
            } catch (e: NoSuchElementException) {
                sizes.values.firstOrNull()
            }
        }

        fun bind(post: Pair<FeedItem, FeedMedia?>?) {
            this.post = post
            this.link = post?.first?.link

            if (post == null) {
                this.post = null
                this.link = null
            } else {
                val item = post.first
                val itemTitle = item.title?.rendered
                val itemExcerpt = item.excerpt?.rendered

                val titleString = when {
                    itemTitle?.isNotBlank() == true -> itemTitle
                    itemExcerpt?.isNotBlank() == true -> itemExcerpt
                    else -> itemView.context.getString(R.string.feed_item_no_title)
                }
                BindingUtils.bindStrippingHtml(title, titleString)
            }

            val media = post?.second
            val variant = post?.second?.selectMediaVariant()
            if (media != null && variant != null) {
                featuredMedia.visibility = View.VISIBLE
                featuredMedia.contentDescription = when {
                    media.caption.rendered.isNotBlank() -> media.caption.rendered
                    else -> featuredMedia.context.getString(R.string.feed_media_content_desctiption)
                }
                glide.load(variant.source_url)
                    .centerInside()
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .fallback(R.drawable.ic_broken_image_black_24dp)
                    .into(featuredMedia)
            } else {
                featuredMedia.visibility = View.INVISIBLE
                featuredMedia.contentDescription = featuredMedia.context
                    .getString(R.string.feed_media_content_desctiption)
                glide.clear(featuredMedia)
            }
        }

        companion object {
            fun create(glide: GlideRequests, parent: ViewGroup): FeedItemHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_item, parent, false)
                return FeedItemHolder(glide, view)
            }
        }
    }

}
