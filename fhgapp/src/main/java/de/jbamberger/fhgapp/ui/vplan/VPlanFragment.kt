package de.jbamberger.fhgapp.ui.vplan

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.api.data.VPlan
import de.jbamberger.api.data.VPlanRow
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.source.Repository
import de.jbamberger.fhgapp.source.Resource
import de.jbamberger.fhgapp.source.Status
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanFragment : BaseFragment<VPlanViewModel>(),
        SwipeRefreshLayout.OnRefreshListener,
        Observer<Pair<Repository.VPlanSettings, Resource<VPlan>>> {

    override val viewModelClass: Class<VPlanViewModel>
        get() = VPlanViewModel::class.java

    private lateinit var binding: RefreshableListFragmentBinding
    private var parent: MainActivity? = null

    override fun onAttach(context: Context?) {
        if (activity is MainActivity) {
            parent = activity as MainActivity;
        } else {
            throw IllegalStateException("Parent must be MainActivity.")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        parent = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater!!, R.layout.refreshable_list_fragment, container, false)
        val layoutManager: LinearLayoutManager = LinearLayoutManager(context)
        binding.container.layoutManager = layoutManager
        binding.container.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        binding.listener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isRefreshing = true
        viewModel.vPlan.observe(this, this)
    }

    override fun onRefresh() {
        binding.isRefreshing = true
        viewModel.vPlan.removeObserver(this)
        viewModel.refresh()
        viewModel.vPlan.observe(this, this)
    }

    override fun onChanged(filteredPlan: Pair<Repository.VPlanSettings, Resource<VPlan>>?) {
        if (filteredPlan == null) return

        parent?.setSubtitle(getSubtitle(filteredPlan.first))
        val vPlanResource = filteredPlan.second
        if (vPlanResource.status == Status.SUCCESS) {
            if (vPlanResource.data != null) {
                binding.container.adapter = VPlanAdapter(vPlanResource.data)
            }
            binding.isRefreshing = false
        } else if (vPlanResource.status == Status.ERROR) {
            binding.isRefreshing = false
        }
    }

    private fun getSubtitle(settings: Repository.VPlanSettings): String {
        return if (settings.showAll || settings.grades.isEmpty()) {
            getString(R.string.vplan_subtitle_all)
        } else {
            if (settings.grades.size > 3) {
                getString(R.string.vplan_subtitle_grades, settings.grades.take(3).joinToString(", "))
            } else {
                getString(R.string.vplan_subtitle_few_grades, settings.grades.take(3).joinToString(", "))
            }
        }
    }

    private class VPlanAdapter internal constructor(vPlan: VPlan) : DataBindingBaseAdapter() {

        private val rows1: List<VPlanRow>
        private val rows2: List<VPlanRow>
        private val header1: VPlanHeader
        private val header2: VPlanHeader
        private val bound1: Int
        private val bound2: Int

        init {
            val day1 = vPlan.day1
            val day2 = vPlan.day2

            rows1 = day1.vPlanRows
            rows2 = day2.vPlanRows
            bound1 = rows1.size + 1
            bound2 = bound1 + rows2.size + 1
            header1 = VPlanHeader(day1.dateAndDay, day1.lastUpdated, day1.motd)
            header2 = VPlanHeader(day2.dateAndDay, day2.lastUpdated, day2.motd)
        }

        override fun getObjForPosition(position: Int): Any? {
            return when (position) {
                0 -> header1
                in 1..(bound1 - 1) -> rows1[position - 1]
                bound1 -> header2
                in (bound1 + 1)..(bound2 - 1) -> rows2[position - bound1 - 1]
                bound2 -> null // footer
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        override fun getListenerForPosition(position: Int): Any? {
            return null
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return when (position) {
                0, bound1 -> R.layout.vplan_header
                in 1..(bound1 - 1), in (bound1 + 1)..(bound2 - 1) -> R.layout.vplan_item
                bound2 -> R.layout.vplan_footer
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        override fun getItemCount(): Int {
            return bound2 + 1
        }
    }
}
