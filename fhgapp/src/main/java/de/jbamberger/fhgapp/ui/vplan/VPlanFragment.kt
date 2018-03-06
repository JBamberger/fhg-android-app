package de.jbamberger.fhgapp.ui.vplan

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.api.data.VPlan
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

    private val adapter: VPlanAdapter = VPlanAdapter()
    private lateinit var binding: RefreshableListFragmentBinding
    private lateinit var loadingErrorSnackBar: Snackbar
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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        loadingErrorSnackBar = Snackbar.make(binding.root, "An error occurred", Snackbar.LENGTH_INDEFINITE)
        val params = loadingErrorSnackBar.view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        loadingErrorSnackBar.view.layoutParams = params


        super.onViewCreated(view, savedInstanceState)
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
        when (vPlanResource.status) {
            Status.LOADING -> {
                if (vPlanResource.data != null) {
                    adapter.setData(vPlanResource.data)
                }
            }
            Status.SUCCESS -> {
                if (vPlanResource.data != null) {
                    adapter.setData(vPlanResource.data)
                }
                loadingErrorSnackBar.dismiss()
                binding.isRefreshing = false
            }
            Status.ERROR -> {
                loadingErrorSnackBar.show()
                binding.isRefreshing = false
            }
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

    private class VPlanAdapter : DataBindingBaseAdapter() {

        private var vPlan: VPlan? = null
        private var header1: VPlanHeader? = null
        private var header2: VPlanHeader? = null
        private var bound1: Int = -1
        private var bound2: Int = -1

        fun setData(vPlan: VPlan?) {
            this.vPlan = vPlan

            if (vPlan != null) {
                val day1 = vPlan.day1
                val day2 = vPlan.day2

                bound1 = day1.vPlanRows.size + 1
                bound2 = bound1 + day1.vPlanRows.size + 1
                header1 = VPlanHeader(day1.dateAndDay, day1.lastUpdated, day1.motd)
                header2 = VPlanHeader(day2.dateAndDay, day2.lastUpdated, day2.motd)
            } else {
                header1 = null
                header2 = null
                bound1 = -1
                bound2 = -1
            }

            notifyDataSetChanged()
        }

        override fun getObjForPosition(position: Int): Any? {
            val vPlan = vPlan ?: return null

            return when (position) {
                0 -> header1
                in 1..(bound1 - 1) -> vPlan.day1.vPlanRows[position - 1]
                bound1 -> header2
                in (bound1 + 1)..(bound2 - 1) -> vPlan.day2.vPlanRows[position - bound1 - 1]
                bound2 -> null // footer
                else -> throw ArrayIndexOutOfBoundsException()
            }
        }

        override fun getListenerForPosition(position: Int): Any? {
            return null
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            if (vPlan == null) return -1 // TODO: notice that nothing was loaded so far
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
