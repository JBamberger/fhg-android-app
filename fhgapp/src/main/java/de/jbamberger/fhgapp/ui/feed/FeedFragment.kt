package de.jbamberger.fhgapp.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.jbamberger.fhg.repository.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhg.repository.util.formatAsAspectRatio
import de.jbamberger.fhg.repository.util.getSaveImgSize
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.di.Injectable
import de.jbamberger.fhgapp.ui.components.BindingUtils
import de.jbamberger.fhgapp.util.GlideApp
import de.jbamberger.fhgapp.util.GlideRequests
import de.jbamberger.fhgapp.util.Utils
import kotlinx.android.synthetic.main.feed_fragment.*
import javax.inject.Inject


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedFragment : Fragment(), Injectable {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var model: FeedViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.feed_fragment, container, false)
        model = getViewModel()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = GlideApp.with(this)
        val adapter = FeedAdapter(glide) { model.retry() }
        feedContainer.adapter = adapter

        model.posts.observe(this, Observer { adapter.submitList(it) })
        model.networkState.observe(this, Observer { adapter.setNetworkState(it) })
        model.refreshState.observe(this, Observer { feedRefresh.isRefreshing = it == NetworkState.LOADING })

        feedRefresh.setOnRefreshListener { model.refresh() }
    }

    private fun getViewModel(): FeedViewModel {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(FeedViewModel::class.java)
    }

    private class FeedAdapter
    internal constructor(
            private val glide: GlideRequests,
            private val retryCallback: () -> Unit)
        : PagedListAdapter<Pair<FeedItem, FeedMedia?>, RecyclerView.ViewHolder>(POST_COMPARATOR) {

        private var networkState: NetworkState? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                R.layout.feed_item -> FeedItemHolder.create(glide, parent)
                R.layout.network_state_item -> ErrorHolder.create(parent, retryCallback)
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
                private val glide: GlideRequests, view: View) : RecyclerView.ViewHolder(view) {
            private val layout = view.findViewById<ConstraintLayout>(R.id.feed_item_content)
            private val title = view.findViewById<TextView>(R.id.title)
            private val content = view.findViewById<TextView>(R.id.content)
            private val featuredMedia = view.findViewById<ImageView>(R.id.featured_media)

            private val textLoading = view.context.getString(R.string.feed_status_loading)

            private var post: Pair<FeedItem, FeedMedia?>? = null

            init {
                view.setOnClickListener {
                    post?.first?.link?.let { link -> Utils.openUrl(view.context, link) }
                }
            }

            fun bind(post: Pair<FeedItem, FeedMedia?>?) {
                this.post = post
                BindingUtils.bindHtml(title, post?.first?.title?.rendered ?: textLoading)
                BindingUtils.bindHtml(content, post?.first?.excerpt?.rendered ?: textLoading)

                val media = post?.second
                val size = media?.getSaveImgSize()
                if (size != null) {
                    featuredMedia.visibility = View.VISIBLE
                    val set = ConstraintSet()
                    set.clone(layout)
                    set.setDimensionRatio(R.id.featured_media, size.formatAsAspectRatio())
                    set.applyTo(layout)

                    glide.load(media)
                            .centerInside()
                            .placeholder(R.drawable.ic_image_black_24dp)
                            .error(R.drawable.ic_broken_image_black_24dp)
                            .fallback(R.drawable.ic_broken_image_black_24dp)
                            .into(featuredMedia)
                    return
                }
                featuredMedia.visibility = View.GONE
                glide.clear(featuredMedia)

            }

            companion object {
                fun create(glide: GlideRequests, parent: ViewGroup): FeedItemHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.feed_item, parent, false)
                    return FeedItemHolder(glide, view)
                }
            }
        }

        private class ErrorHolder(view: View, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {
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
}
