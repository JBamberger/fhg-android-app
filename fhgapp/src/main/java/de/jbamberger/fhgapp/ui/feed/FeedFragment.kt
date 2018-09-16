package de.jbamberger.fhgapp.ui.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import de.jbamberger.fhg.repository.api.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.util.FeedMediaRequest
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
        initAdapter()
        initSwipeRefresh()
    }

    private fun getViewModel(): FeedViewModel {
        return ViewModelProviders.of(this, viewModelFactory)
                .get(FeedViewModel::class.java)
    }

    private fun initSwipeRefresh() {
        model.refreshState.observe(this, Observer {
            feedRefresh.isRefreshing = it == NetworkState.LOADING
        })
        feedRefresh.setOnRefreshListener {
            model.refresh()
        }
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = FeedAdapter(glide) {
            model.retry()
        }
        feedContainer.adapter = adapter
        model.posts.observe(this, Observer {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

    }

    private class FeedAdapter
    internal constructor(
            private val glide: GlideRequests,
            private val retryCallback: () -> Unit)
        : PagedListAdapter<FeedItem, RecyclerView.ViewHolder>(POST_COMPARATOR) {

        private var networkState: NetworkState? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                R.layout.feed_item -> FeedItemHolder.create(glide, parent)
                R.layout.network_state_item -> ErrorHolder.create(parent, retryCallback)
                else -> throw IllegalArgumentException("unknown view Type $viewType")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                R.layout.feed_item -> (holder as FeedItemHolder).bind(getItem(position))
                R.layout.network_state_item -> (holder as ErrorHolder).bind(networkState)
            }
        }

        override fun getItemCount(): Int {
            return super.getItemCount() + if (hasExtraRow()) 1 else 0
        }

        override fun getItemViewType(position: Int): Int {
            return if (hasExtraRow() && position == itemCount - 1) {
                R.layout.network_state_item
            } else {
                R.layout.feed_item
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
            val POST_COMPARATOR = object : DiffUtil.ItemCallback<FeedItem>() {
                override fun areItemsTheSame(oldItem: FeedItem?, newItem: FeedItem?) =
                        oldItem?.id == newItem?.id

                override fun areContentsTheSame(oldItem: FeedItem?, newItem: FeedItem?) = oldItem == newItem
            }
        }

        private class FeedItemHolder(
                private val glide: GlideRequests,
                view: View) : RecyclerView.ViewHolder(view) {
            private val title = view.findViewById<TextView>(R.id.title)
            private val content = view.findViewById<TextView>(R.id.content)
            private val featuredMedia = view.findViewById<ImageView>(R.id.featured_media)

            private val textLoading = view.context.getString(R.string.feed_status_loading)

            private var post: FeedItem? = null

            init {
                view.setOnClickListener {
                    post?.link?.let {
                        Utils.openUrl(view.context, it)
                    }

                }
            }

            fun bind(post: FeedItem?) {
                this.post = post
                BindingUtils.bindHtml(title, post?.title?.rendered ?: textLoading)
                BindingUtils.bindHtml(content, post?.excerpt?.rendered ?: textLoading)

                val media = post?.featuredMedia
                if (media != null && media > 0) {
                    featuredMedia.visibility = View.VISIBLE

                    glide.load(FeedMediaRequest(media))
                            .centerInside()
                            .placeholder(R.drawable.ic_image_black_24dp)
                            .error(R.drawable.ic_broken_image_black_24dp)
                            .fallback(R.drawable.ic_broken_image_black_24dp)
                            .into(featuredMedia)
                } else {
                    featuredMedia.visibility = View.GONE
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

        private class ErrorHolder(view: View, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {
            private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
            private val retry = view.findViewById<Button>(R.id.retry_button)
            private val errorMsg = view.findViewById<TextView>(R.id.error_msg)

            init {
                retry.setOnClickListener {
                    retryCallback()
                }
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
