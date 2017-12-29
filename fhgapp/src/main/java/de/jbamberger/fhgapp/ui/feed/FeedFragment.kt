package de.jbamberger.fhgapp.ui.feed


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.source.Resource
import de.jbamberger.fhgapp.source.Status
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedFragment : BaseFragment<FeedViewModel>(),
        SwipeRefreshLayout.OnRefreshListener, Observer<Resource<FeedChunk>> {

    class Feed

    private var binding: RefreshableListFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater!!, R.layout.refreshable_list_fragment, container, false)
        binding!!.container.layoutManager = LinearLayoutManager(context)
        binding!!.listener = this
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.init()
        binding!!.isRefreshing = true
        viewModel.feed!!.observe(this, this)
    }

    override fun onRefresh() {
        binding!!.isRefreshing = true
        viewModel.feed!!.removeObserver(this)
        viewModel.refresh()
        viewModel.feed!!.observe(this, this)
    }

    override fun onChanged(feedResource: Resource<FeedChunk>?) {
        if (feedResource == null) return
        if (feedResource.status == Status.SUCCESS && feedResource.data != null) {
            binding!!.container.adapter = FeedAdapter(feedResource.data)
            binding!!.isRefreshing = false
        }
    }

    override fun getViewModelClass(): Class<FeedViewModel> {
        return FeedViewModel::class.java
    }

    private class FeedAdapter internal constructor(feed: FeedChunk) : DataBindingBaseAdapter() {

        override fun getLayoutIdForPosition(position: Int): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getObjForPosition(position: Int): Any {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getListenerForPosition(position: Int): Any {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
