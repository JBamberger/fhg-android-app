package de.jbamberger.fhgapp.ui.vplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.jbamberger.api.data.VPlan
import de.jbamberger.fhgapp.source.Repository
import de.jbamberger.fhgapp.source.Resource
import de.jbamberger.fhgapp.source.Status
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    internal var vPlan = filterVPlan(repo.vPlan)

    internal fun refresh() {
        vPlan = filterVPlan(repo.vPlan)
    }

    private fun filterVPlan(unfiltered: LiveData<Resource<VPlan>>):
            LiveData<Pair<Repository.VPlanSettings, Resource<VPlan>>> {
        return Transformations.map(unfiltered, {
            val settings = repo.vPlanSettings
            val matcher = VPlanUtils.getVPlanMatcher(settings)
            if (it.status == Status.SUCCESS && it.data != null) {
                Pair(settings, Resource.success(VPlanUtils.filter(it.data, matcher)))
            } else {
                Pair(settings, it)
            }
        })
    }
}