package de.jbamberger.fhgapp.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhgapp.ui.contact.ContactFragment
import de.jbamberger.fhgapp.ui.feed.FeedFragment
import de.jbamberger.fhgapp.ui.vplan.VPlanFragment
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@HiltViewModel
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