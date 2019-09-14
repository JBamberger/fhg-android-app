package de.jbamberger.fhgapp.ui.vplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhgapp.Settings
import timber.log.Timber
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

    private fun filteredVPlan(): LiveData<Pair<Settings.VPlanSettings, Resource<VPlan>>> {
        return map(repo.getVPlan()) {
            Timber.d("Result: %s", it)

            val settings = settings.vPlanSettings

            return@map Pair(settings, when (it) {
                is Resource.Success -> Resource.Success(
                        VPlanUtils.filter(it.data, VPlanUtils.getVPlanMatcher(settings)))
                else -> it
            })
        }
    }
}