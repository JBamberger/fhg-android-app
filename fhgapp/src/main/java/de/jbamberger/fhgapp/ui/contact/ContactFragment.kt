package de.jbamberger.fhgapp.ui.contact

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ContactFragmentBinding
import de.jbamberger.fhgapp.ui.components.BaseFragment

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class ContactFragment : BaseFragment<ContactViewModel>() {

    private var binding: ContactFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater!!, R.layout.contact_fragment, container, false)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.init()
    }



    override fun getViewModelClass(): Class<ContactViewModel> {
        return ContactViewModel::class.java
    }
}
