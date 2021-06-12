package de.jbamberger.fhgapp.ui.vplan

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter
import de.jbamberger.fhgapp.ui.feed.FeedViewModel
import de.jbamberger.fhgapp.util.Utils
import timber.log.Timber


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@AndroidEntryPoint
class VPlanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: VPlanViewModel by viewModels()

    private val adapter: VPlanAdapter = VPlanAdapter()
    private lateinit var binding: RefreshableListFragmentBinding
    private var parent: MainActivity? = null

    override fun onAttach(context: Context) {
        parent = when (activity) {
            is MainActivity -> activity as MainActivity
            else -> throw IllegalStateException("Parent must be MainActivity.")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        parent = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.refreshable_list_fragment, container, false)
        val layoutManager = LinearLayoutManager(context)
        binding.container.layoutManager = layoutManager
        binding.container.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        binding.container.adapter = adapter
        binding.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.plan.observe(viewLifecycleOwner, {
            if (it != null) adapter.setData(it)
            Timber.d("plan update: %s", it)
        })
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (it != null) binding.isRefreshing = it
            Timber.d("refreshing update: %s", it)
        })
        viewModel.title.observe(viewLifecycleOwner, {
            if (it != null) parent?.setSubtitle(it)
            Timber.d("title update: %s", it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vplan, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_show_vplan) {
            Utils.openUrl(requireContext(), R.string.vplan_link)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

    private class VPlanAdapter : DataBindingBaseAdapter() {
        private var content: List<VPlanListItem> = emptyList()

        fun setData(plan: List<VPlanListItem>) {
            content = plan
            notifyDataSetChanged()
        }

        override fun getItemCount() = content.size
        override fun getObjForPosition(position: Int) = content[position].getData()
        override fun getLayoutIdForPosition(position: Int) = content[position].getLayoutId()
    }
}
