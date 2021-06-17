package de.jbamberger.fhgapp.ui.vplan

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.VplanFragmentBinding
import de.jbamberger.fhgapp.ui.DataBindingBaseAdapter
import de.jbamberger.fhgapp.ui.MainActivity
import de.jbamberger.fhgapp.util.Utils


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@AndroidEntryPoint
class VPlanFragment : Fragment() {

    private val viewModel: VPlanViewModel by viewModels()

    private var _binding: VplanFragmentBinding? = null
    private val binding get() = _binding!!

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = VplanFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)

        val adapter = VPlanAdapter()

        binding.vplanContainer.layoutManager = layoutManager
        binding.vplanContainer.addItemDecoration(
            DividerItemDecoration(context, layoutManager.orientation)
        )
        binding.vplanContainer.adapter = adapter
        binding.vplanRefresh.setOnRefreshListener(viewModel::refresh)

        viewModel.plan.observe(viewLifecycleOwner, {
            if (it != null) adapter.setData(it)
//            Timber.d("plan update: %s", it)
        })
        viewModel.refreshing.observe(viewLifecycleOwner, {
            if (it != null) binding.vplanRefresh.isRefreshing = it
//            Timber.d("refreshing update: %s", it)
        })
        viewModel.title.observe(viewLifecycleOwner, {
            if (it != null) parent?.setSubtitle(it)
//            Timber.d("title update: %s", it)
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
