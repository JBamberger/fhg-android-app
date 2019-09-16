package de.jbamberger.fhgapp.ui.vplan

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.Settings
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter
import de.jbamberger.fhgapp.util.Utils


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanFragment : BaseFragment<VPlanViewModel>(),
        SwipeRefreshLayout.OnRefreshListener,
        Observer<Pair<Settings.VPlanSettings, Resource<List<VPlanListItem>>>> {

    override val viewModelClass: Class<VPlanViewModel>
        get() = VPlanViewModel::class.java

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.refreshable_list_fragment, container, false)
        val layoutManager = LinearLayoutManager(context)
        binding.container.layoutManager = layoutManager
        binding.container.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        binding.container.adapter = adapter
        binding.listener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isRefreshing = true
        viewModel.vPlan.observe(this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vplan, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_show_vplan) {
            Utils.openUrl(context!!, R.string.vplan_link)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        binding.isRefreshing = true
        viewModel.vPlan.removeObserver(this)
        viewModel.refresh()
        viewModel.vPlan.observe(this, this)
    }

    override fun onChanged(filteredPlan: Pair<Settings.VPlanSettings, Resource<List<VPlanListItem>>>?) {
        if (filteredPlan == null) return

        parent?.setSubtitle(getSubtitle(filteredPlan.first))
        val vPlanResource = filteredPlan.second
        when (vPlanResource) {
            is Resource.Loading -> {
                adapter.setData(false, vPlanResource.data ?: emptyList())
            }
            is Resource.Success -> {
                adapter.setData(false, vPlanResource.data)
                binding.isRefreshing = false
            }
            is Resource.Error -> {
                adapter.setData(true, vPlanResource.data?: emptyList())
                binding.isRefreshing = false
            }
        }
    }

    private fun getSubtitle(settings: Settings.VPlanSettings): String {
        return if (settings.showAll || settings.grades.isEmpty()) {
            getString(R.string.vplan_subtitle_all)
        } else {
            getString(R.string.vplan_subtitle_grades,
                    settings.grades.joinToString(separator = ", ", limit = 3))
        }
    }

    private class VPlanAdapter : DataBindingBaseAdapter() {

        private var content: List<VPlanListItem> = emptyList()

        fun setData(showWarning: Boolean, plan: List<VPlanListItem>) {
            content = if (showWarning) {
                val tmp = mutableListOf<VPlanListItem>()
                tmp.add(VPlanListItem.Warning)
                tmp.addAll(plan)
                tmp
            } else {
                plan
            }
            notifyDataSetChanged()
        }

        override fun getItemCount() = content.size
        override fun getObjForPosition(position: Int) = content[position].getData()
        override fun getLayoutIdForPosition(position: Int) = content[position].getLayoutId()
        override fun getListenerForPosition(position: Int): Nothing? = null
    }
}
