package de.jbamberger.fhgapp.ui.vplan

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhgapp.App
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.Settings
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanViewModel @Inject
internal constructor(
        app: App,
        private val settings: Settings,
        private val repo: Repository) : AndroidViewModel(app) {

    private val _plan = MediatorLiveData<List<VPlanListItem>>()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _refreshing: MutableLiveData<Boolean> = MutableLiveData()

    internal val plan: LiveData<List<VPlanListItem>>
        get() = _plan
    internal val title: LiveData<String>
        get() = _title
    internal val refreshing: LiveData<Boolean>
        get() = _refreshing

    init {
        refresh()
    }

    internal fun refresh() {
        val settings = settings.vPlanSettings

        _title.value = getSubtitle(settings)
        _refreshing.value = true


        val update = repo.getVPlan()
        _plan.addSource(update) {
            if (it == null) return@addSource

            val vpMatcher = VPlanUtils.getVPlanMatcher(settings)

            _plan.value = when (it) {
                is Resource.Success -> {
                    _refreshing.value = false
                    _plan.removeSource(update)
                    VPlanUtils.filter(it.data, vpMatcher)
                }
                is Resource.Loading -> it.data?.let { VPlanUtils.filter(it, vpMatcher) }
                        ?: emptyList()
                is Resource.Error -> {
                    val tmp = mutableListOf<VPlanListItem>()
                    tmp.add(VPlanListItem.Warning)
                    it.data?.let { tmp.addAll(VPlanUtils.filter(it, vpMatcher)) }
                    _plan.removeSource(update)
                    tmp
                }
            }
        }
    }

    private fun getSubtitle(settings: Settings.VPlanSettings): String {
        return if (settings.showAll || settings.grades.isEmpty()) {
            getApplication<App>().getString(R.string.vplan_subtitle_all)
        } else {
            getApplication<App>().getString(R.string.vplan_subtitle_grades,
                    settings.grades.joinToString(separator = ", ", limit = 3))
        }
    }
}