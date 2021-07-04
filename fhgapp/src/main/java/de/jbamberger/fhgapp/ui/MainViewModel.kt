/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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