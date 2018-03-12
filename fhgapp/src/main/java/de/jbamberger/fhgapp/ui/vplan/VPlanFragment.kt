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
import de.jbamberger.api.data.VPlanHeader
import de.jbamberger.api.data.VPlanRow
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.RefreshableListFragmentBinding
import de.jbamberger.fhgapp.Settings
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.ui.components.BaseFragment
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanFragment : BaseFragment<VPlanViewModel>(),
        SwipeRefreshLayout.OnRefreshListener,
        Observer<Pair<Settings.VPlanSettings, Resource<VPlan>>> {

    override val viewModelClass: Class<VPlanViewModel>
        get() = VPlanViewModel::class.java

    private val adapter: VPlanAdapter = VPlanAdapter()
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

    override fun onRefresh() {
        binding.isRefreshing = true
        viewModel.vPlan.removeObserver(this)
        viewModel.refresh()
        viewModel.vPlan.observe(this, this)
    }

    override fun onChanged(filteredPlan: Pair<Settings.VPlanSettings, Resource<VPlan>>?) {
        if (filteredPlan == null) return

        parent?.setSubtitle(getSubtitle(filteredPlan.first))
        val vPlanResource = filteredPlan.second
        when (vPlanResource) {
            is Resource.Loading -> {
                adapter.setData(false, vPlanResource.data)
            }
            is Resource.Success -> {
                adapter.setData(false, vPlanResource.data)
                binding.isRefreshing = false
            }
            is Resource.Error -> {
                adapter.setData(true, vPlanResource.data)
                binding.isRefreshing = false
            }
        }
    }

    private fun getSubtitle(settings: Settings.VPlanSettings): String {
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

    private class VPlanAdapter : DataBindingBaseAdapter() {

        private var showWarning = true
        private var indexedPlan: IndexedVPlan? = null

        fun setData(showWarning: Boolean, vPlan: VPlan?) {
            this.showWarning = showWarning
            indexedPlan = when {
                vPlan != null -> IndexedVPlan(vPlan)
                else -> null
            }

            notifyDataSetChanged()
        }

        override fun getObjForPosition(position: Int): Any? {
            val p = indexedPlan
            return if (showWarning) {
                when {
                    position == 0 -> null
                    p != null -> p[position - 1]
                    else -> throw ArrayIndexOutOfBoundsException()
                }
            } else {
                if (p != null) {
                    p[position]
                } else {
                    throw ArrayIndexOutOfBoundsException()
                }
            }
        }

        override fun getListenerForPosition(position: Int): Any? {
            return null
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            val p = indexedPlan
            return if (showWarning) {
                if (position == 0) {
                    return R.layout.list_flat_error
                } else if (p != null) {
                    p.getLayout(position - 1)
                } else {
                    throw ArrayIndexOutOfBoundsException()
                }
            } else {
                if (p != null) {
                    p.getLayout(position)
                } else {
                    throw ArrayIndexOutOfBoundsException()
                }
            }
        }

        override fun getItemCount(): Int {
            val p = indexedPlan
            var c = 0
            if (showWarning) c += 1
            if (p != null) c += p.size

            return c
        }

        private class IndexedVPlan(plan: VPlan) {

            val size = plan.day1.vPlanRows.size + plan.day2.vPlanRows.size + 3

            private val header1: VPlanHeader
            private val rows1: List<VPlanRow>
            private val rows2: List<VPlanRow>
            private val header2: VPlanHeader
            private val bound1: Int
            private val bound2: Int

            init {
                val day1 = plan.day1
                val day2 = plan.day2

                rows1 = day1.vPlanRows
                rows2 = day2.vPlanRows
                bound1 = rows1.size + 1
                bound2 = bound1 + rows2.size + 1
                header1 = day1.header
                header2 = day2.header
            }


            operator fun get(position: Int): Any? {
                if (position < 0 || position >= size) throw ArrayIndexOutOfBoundsException()

                return when (position) {
                    0 -> header1
                    in 1..(bound1 - 1) -> rows1[position - 1]
                    bound1 -> header2
                    in (bound1 + 1)..(bound2 - 1) -> rows2[position - bound1 - 1]
                    bound2 -> null // footer
                    else -> throw ArrayIndexOutOfBoundsException()
                }
            }

            fun getLayout(position: Int): Int {
                return when (position) {
                    0, bound1 -> R.layout.vplan_header
                    in 1..(bound1 - 1), in (bound1 + 1)..(bound2 - 1) -> R.layout.vplan_item
                    bound2 -> R.layout.vplan_footer
                    else -> throw ArrayIndexOutOfBoundsException()
                }
            }
        }
    }
}
