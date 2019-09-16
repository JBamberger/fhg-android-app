package de.jbamberger.fhgapp.ui.vplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhgapp.Settings
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(
        private val settings: Settings,
        private val repo: Repository) : ViewModel() {
    internal var vPlan = filteredVPlan()

    internal fun refresh() {
        vPlan = filteredVPlan()
    }

    private fun filteredVPlan(): LiveData<Pair<Settings.VPlanSettings, Resource<List<VPlanListItem>>>> {
        return map(repo.getVPlan()) {
            val settings = settings.vPlanSettings

            return@map Pair(settings, when (it) {
                is Resource.Success -> Resource.Success(VPlanUtils.filterV2(it.data, VPlanUtils.getVPlanMatcher(settings)))
                is Resource.Loading -> Resource.Loading(it.data?.let { VPlanUtils.filterV2(it, VPlanUtils.getVPlanMatcher(settings)) })
                is Resource.Error -> Resource.Error(it.message, it.data?.let { VPlanUtils.filterV2(it, VPlanUtils.getVPlanMatcher(settings)) })
            })
        }
    }
}