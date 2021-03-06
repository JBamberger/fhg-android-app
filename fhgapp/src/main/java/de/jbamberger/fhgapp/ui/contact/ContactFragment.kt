package de.jbamberger.fhgapp.ui.contact

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ContactFragmentBinding


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@AndroidEntryPoint
class ContactFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: ContactFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.contact_fragment, container, false)
        binding.listener = this

        val callDrawable = AppCompatResources.getDrawable(
            binding.root.context, R.drawable.ic_contact_call_accent_24dp
        )
        val mailDrawable = AppCompatResources.getDrawable(
            binding.root.context, R.drawable.ic_contact_email_accent_24dp
        )
        val mapDrawable = AppCompatResources.getDrawable(
            binding.root.context, R.drawable.ic_contact_map_accent_24dp
        )
        binding.contactActionCall.setCompoundDrawablesWithIntrinsicBounds(
            null, callDrawable, null, null
        )
        binding.contactActionMail.setCompoundDrawablesWithIntrinsicBounds(
            null, mailDrawable, null, null
        )
        binding.contactActionNavigate.setCompoundDrawablesWithIntrinsicBounds(
            null, mapDrawable, null, null
        )
        return binding.root
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.contact_action_call -> {
                safeStartActivity(
                    Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.contact_phone_uri)))
                )
            }
            R.id.contact_action_mail -> {
                safeStartActivity(
                    Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.contact_mail_uri)))
                )
            }
            R.id.contact_action_navigate -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.contact_navigation_uri))
                intent.`package` = getString(R.string.contact_navigation_package)
                safeStartActivity(intent)
            }
        }
    }


    private fun safeStartActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.contact_action_activity_not_found, Toast.LENGTH_LONG)
                .show()
        }
    }
}
