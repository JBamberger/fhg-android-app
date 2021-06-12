package de.jbamberger.fhgapp.ui.vplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhg.repository.Repository
import de.jbamberger.fhg.repository.Resource
import de.jbamberger.fhgapp.App
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.Settings
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@HiltViewModel
class VPlanViewModel @Inject
internal constructor(
    app: Application,
    private val settings: Settings,
    private val repo: Repository
) : AndroidViewModel(app) {

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
        _plan.addSource(update) { resource ->
            if (resource == null) return@addSource

            val vpMatcher = VPlanUtils.getVPlanMatcher(settings)

            _plan.value = when (resource) {
                is Resource.Success -> {
                    _refreshing.value = false
                    _plan.removeSource(update)
                    VPlanUtils.filter(resource.data, vpMatcher)
                }
                is Resource.Loading -> resource.data?.let { VPlanUtils.filter(it, vpMatcher) }
                    ?: emptyList()
                is Resource.Error -> {
                    _refreshing.value = false
                    val tmp = mutableListOf<VPlanListItem>()
                    tmp.add(VPlanListItem.Warning)
                    resource.data?.let { tmp.addAll(VPlanUtils.filter(it, vpMatcher)) }
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
            getApplication<App>().getString(
                R.string.vplan_subtitle_grades,
                settings.grades.joinToString(separator = ", ", limit = 3)
            )
        }
    }
}