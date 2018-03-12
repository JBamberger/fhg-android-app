package de.jbamberger.fhgapp.ui.feed


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.api.data.FeedItem
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter
import de.jbamberger.fhgapp.util.Utils


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedFragment : BaseFragment<FeedViewModel>(),
        SwipeRefreshLayout.OnRefreshListener, Observer<Resource<List<FeedItem>>> {

    override val viewModelClass = FeedViewModel::class.java
    private val adapter = FeedAdapter(this)
    private lateinit var binding: RefreshableListFragmentBinding

    override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater!!, R.layout.refreshable_list_fragment, container, false)
        binding.container.layoutManager = LinearLayoutManager(context)
        binding.container.adapter = adapter
        binding.listener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isRefreshing = true
        viewModel.feed.observe(this, this)
    }

    override fun onRefresh() {
        binding.isRefreshing = true
        viewModel.feed.removeObserver(this)
        viewModel.refresh()
        viewModel.feed.observe(this, this)
    }

    override fun onChanged(feedResource: Resource<List<FeedItem>>?) {
        if (feedResource == null) return

        when(feedResource) {
            is Resource.Loading -> {
                adapter.setData(false, feedResource.data)
            }
            is Resource.Success -> {
                adapter.setData(false, feedResource.data)
                binding.isRefreshing = false
            }
            is Resource.Error -> {
                adapter.setData(true, feedResource.data)
                binding.isRefreshing = false
            }
        }
    }

    fun itemClicked(url: String) {
        Utils.openUrl(activity, url)
    }

    private class FeedAdapter
    internal constructor(private val fragment: FeedFragment) : DataBindingBaseAdapter() {

        private var showWarning = true
        private var feed: List<FeedItem> = emptyList()

        fun setData(showWarning: Boolean, feed: List<FeedItem>?) {
            this.showWarning = showWarning
            this.feed = feed ?: emptyList()
            notifyDataSetChanged()
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            if (showWarning && position == 0) return R.layout.list_card_error
            return R.layout.feed_item
        }

        override fun getItemCount(): Int {
            return if (showWarning) {
                feed.size + 1
            } else {
                feed.size
            }
        }

        override fun getObjForPosition(position: Int): Any? {
            if (showWarning && position == 0) return null
            return if (showWarning) {
                feed[position - 1]
            } else {
                feed[position]
            }
        }

        override fun getListenerForPosition(position: Int): Any? {
            if (showWarning && position == 0) return null
            return fragment
        }
    }
}
