package de.jbamberger.fhgapp.ui.vplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import de.jbamberger.api.data.VPlan
import de.jbamberger.fhgapp.source.Repository
import de.jbamberger.fhgapp.source.Resource
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    internal var vPlan: LiveData<Resource<VPlan>>? = null
        private set


    internal fun init() {
        if (this.vPlan != null) {
            return
        }
        vPlan = repo.vPlan
    }

    internal fun refresh() {
        vPlan = repo.vPlan
    }
}