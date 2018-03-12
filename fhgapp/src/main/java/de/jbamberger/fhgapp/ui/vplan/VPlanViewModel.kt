package de.jbamberger.fhgapp.ui.vplan

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.jbamberger.api.data.VPlan
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhg.repository.Status
import de.jbamberger.fhgapp.Settings
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(
        private val settings: Settings,
        private val repo: Repository) : ViewModel() {
    internal var vPlan = filterVPlan(repo.vPlan)

    internal fun refresh() {
        vPlan = filterVPlan(repo.vPlan)
    }

    private fun filterVPlan(unfiltered: LiveData<Resource<VPlan>>):
            LiveData<Pair<Settings.VPlanSettings, Resource<VPlan>>> {
        return Transformations.map(unfiltered, {
            val settings = settings.vPlanSettings
            val matcher = VPlanUtils.getVPlanMatcher(settings)
            val plan = it.data
            if (it.status == Status.SUCCESS && plan != null) {
                Pair(settings, Resource.success(VPlanUtils.filter(plan, matcher)))
            } else {
                Pair(settings, it)
            }
        })
    }
}