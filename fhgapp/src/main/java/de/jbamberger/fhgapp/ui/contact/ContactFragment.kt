package de.jbamberger.fhgapp.ui.contact

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
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

class ContactFragment : BaseFragment<ContactViewModel>(), View.OnClickListener {

    private lateinit var binding: ContactFragmentBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater!!, R.layout.contact_fragment, container, false)
        binding.listener = this
        return binding.root
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.contact_action_call -> {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.contact_phone_uri))))
            }
            R.id.contact_action_mail -> {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.contact_mail_uri))))
            }
            R.id.contact_action_navigate -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.contact_navigation_uri))
                intent.`package` = getString(R.string.contact_navigation_package)
                startActivity(intent)
            }
        }
    }

    override val viewModelClass: Class<ContactViewModel>
        get() = ContactViewModel::class.java
}
