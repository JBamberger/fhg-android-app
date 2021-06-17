package de.jbamberger.fhgapp.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.databinding.FeedFragmentBinding
import de.jbamberger.fhgapp.util.GlideApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val model: FeedViewModel by viewModels()

    private var _binding: FeedFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FeedFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glide = GlideApp.with(this)
        val pagingAdapter = FeedAdapter(glide)
        val refreshStateAdapter = FeedLoadStateAdapter(pagingAdapter::retry)
        val footerStateAdapter = FeedLoadStateAdapter(pagingAdapter::retry)

        pagingAdapter.addLoadStateListener { loadStates ->
            refreshStateAdapter.loadState = loadStates.refresh
            footerStateAdapter.loadState = loadStates.append
        }
        val errorShowingAdapter =
            ConcatAdapter(refreshStateAdapter, pagingAdapter, footerStateAdapter)

        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.feedRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        val layoutManager = LinearLayoutManager(context)
        binding.feedContainer.layoutManager = layoutManager
        binding.feedContainer.adapter = errorShowingAdapter
        binding.feedContainer.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        binding.feedRefresh.setOnRefreshListener(pagingAdapter::refresh)
        viewLifecycleOwner.lifecycleScope.launch {
            model.feed.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }
    }
}
