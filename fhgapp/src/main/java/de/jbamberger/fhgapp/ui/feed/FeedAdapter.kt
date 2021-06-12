package de.jbamberger.fhgapp.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.repository.NetworkState
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.ui.components.BindingUtils
import de.jbamberger.fhgapp.util.GlideRequests
import de.jbamberger.fhgapp.util.Utils

internal class FeedAdapter(
    private val glide: GlideRequests
) : PagingDataAdapter<Pair<FeedItem, FeedMedia?>, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.feed_item -> FeedItemHolder.create(glide, parent)
            R.layout.network_state_item -> ErrorHolder.create(parent, this::retry)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedItemHolder -> holder.bind(getItem(position))
            is ErrorHolder -> holder.bind(networkState)
            else -> throw IllegalArgumentException("Unknown ViewHolder type ${holder.javaClass}")
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            hasExtraRow() && position == itemCount - 1 -> R.layout.network_state_item
            else -> R.layout.feed_item
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
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

    private class FeedItemHolder(
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

    private class ErrorHolder(view: View, private val retryCallback: () -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        private val retry = view.findViewById<Button>(R.id.retry_button)
        private val errorMsg = view.findViewById<TextView>(R.id.error_msg)

        init {
            retry.setOnClickListener { retryCallback() }
        }

        fun bind(networkState: NetworkState?) {
            BindingUtils.bindVisibility(progressBar, networkState is NetworkState.LOADING)
            val isError = networkState is NetworkState.ERROR
            BindingUtils.bindVisibility(retry, isError)
            BindingUtils.bindVisibility(errorMsg, isError)
            errorMsg.text = if (isError) (networkState as NetworkState.ERROR).message else null
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): ErrorHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.network_state_item, parent, false)
                return ErrorHolder(view, retryCallback)
            }
        }
    }
}
