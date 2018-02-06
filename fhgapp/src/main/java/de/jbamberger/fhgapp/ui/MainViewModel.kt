package de.jbamberger.fhgapp.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import de.jbamberger.fhgapp.ui.contact.ContactFragment
import de.jbamberger.fhgapp.ui.feed.FeedFragment
import de.jbamberger.fhgapp.ui.vplan.VPlanFragment
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class MainViewModel @Inject constructor() : ViewModel() {
    private val fragment: MutableLiveData<Fragment> = MutableLiveData()

    fun selectedVPlan() {
        fragment.value = VPlanFragment()
    }

    fun selectedFeed() {
        fragment.value = FeedFragment()
    }

    fun selectedContact() {
        fragment.value = ContactFragment()
    }

    fun getFragment(): LiveData<Fragment> {
        return fragment
    }

    init {
        fragment.value = VPlanFragment()
    }
}