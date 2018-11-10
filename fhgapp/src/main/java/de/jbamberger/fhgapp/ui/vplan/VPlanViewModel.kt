package de.jbamberger.fhgapp.ui.vplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhgapp.Settings
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(
        private val settings: Settings,
        private val repo: Repository) : ViewModel() {
    internal var vPlan = filterVPlan(repo.getVPlan())

    internal fun refresh() {
        vPlan = filterVPlan(repo.getVPlan())
    }

    private fun filterVPlan(unfiltered: LiveData<Resource<VPlan>>):
            LiveData<Pair<Settings.VPlanSettings, Resource<VPlan>>> {
        return Transformations.map(unfiltered, {
            val settings = settings.vPlanSettings
            Pair(settings, when (it) {
                is Resource.Success -> Resource.Success(
                        VPlanUtils.filter(it.data, VPlanUtils.getVPlanMatcher(settings)))
                else -> it
            })
        })
    }
}