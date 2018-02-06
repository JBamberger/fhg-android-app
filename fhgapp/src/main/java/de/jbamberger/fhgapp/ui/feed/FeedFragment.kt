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
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.source.Resource
import de.jbamberger.fhgapp.source.Status
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter
import de.jbamberger.fhgapp.util.Utils


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedFragment : BaseFragment<FeedViewModel>(),
        SwipeRefreshLayout.OnRefreshListener, Observer<Resource<List<FeedItem>>> {

    override val viewModelClass: Class<FeedViewModel>
        get() = FeedViewModel::class.java

    private lateinit var binding: RefreshableListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater!!, R.layout.refreshable_list_fragment, container, false)
        binding.container.layoutManager = LinearLayoutManager(context)
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
        if (feedResource.status == Status.SUCCESS) {
            val items = feedResource.data
            if (items != null) {
                binding.container.adapter = FeedAdapter(this, items)
            }
            binding.isRefreshing = false
        } else if (feedResource.status == Status.ERROR) {
            binding.isRefreshing = false
        }
    }

    fun itemClicked(url: String) {
        Utils.openUrl(activity, url)
    }

    private class FeedAdapter
    internal constructor(private val fragment: FeedFragment, private val feed: List<FeedItem>)
        : DataBindingBaseAdapter() {

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.feed_item
        }

        override fun getItemCount(): Int {
            return feed.size
        }

        override fun getObjForPosition(position: Int): Any {
            return feed[position]
        }

        override fun getListenerForPosition(position: Int): Any? {
            return fragment
        }
    }
}
