package de.jbamberger.fhgapp.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import de.jbamberger.api.ApiResponse
import de.jbamberger.api.FhgApi
import de.jbamberger.api.data.FeedItem
import de.jbamberger.api.data.VPlan
import de.jbamberger.fhgapp.AppExecutors
import de.jbamberger.fhgapp.source.db.AppDatabase
import de.jbamberger.fhgapp.source.db.KeyValueStorage
import de.jbamberger.fhgapp.source.db.Settings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
class Repository @Inject
constructor(
        private val appExecutors: AppExecutors,
        private val api: FhgApi,
        private val db: AppDatabase,
        private val kvStore: KeyValueStorage,
        private val settings: Settings) {

    val vPlan: LiveData<Resource<VPlan>>
        get() = NetworkBoundResource(appExecutors, object : NetworkBoundResource.Provider<VPlan, VPlan> {
            var vplanFromNet = true

            override fun onFetchFailed() {
                vplanFromNet = true
            }

            override fun saveCallResult(item: VPlan) {
                kvStore.save(VPLAN_KEY, item)
            }

            override fun shouldFetch(data: VPlan?): Boolean {
                val fetch = vplanFromNet
                vplanFromNet = false
                return fetch
            }

            override fun loadFromDb(): LiveData<VPlan> {
                val l = MutableLiveData<VPlan>()
                l.value = kvStore.get(VPLAN_KEY)
                return l
            }

            override fun createCall(): LiveData<ApiResponse<VPlan>> {
                val m = MediatorLiveData<ApiResponse<VPlan>>()
                m.addSource(api.vPlan, m::setValue)
                return m
            }
        }).asLiveData()

    val feed: LiveData<Resource<List<FeedItem>>>
        get() = NetworkBoundResource(appExecutors, object : NetworkBoundResource.Provider<List<FeedItem>, List<FeedItem>> {

            var feedFromNet: Boolean = true

            override fun onFetchFailed() {
                feedFromNet = true
            }

            override fun saveCallResult(item: List<FeedItem>) {
                db.feedItemDao.insertAll(item)
            }

            override fun shouldFetch(data: List<FeedItem>?): Boolean {
                val fetch = feedFromNet
                feedFromNet = false
                return fetch
            }

            override fun loadFromDb(): LiveData<List<FeedItem>> {
                return db.feedItemDao.getAll()
            }

            override fun createCall(): LiveData<ApiResponse<List<FeedItem>>> {
                val m = MediatorLiveData<ApiResponse<List<FeedItem>>>()
                m.addSource(api.feed, m::setValue)
                return m
            }
        }).asLiveData()

    val vPlanSettings: VPlanSettings
        get() = VPlanSettings(
                settings.vPlanShowAll,
                settings.vPlanGrades,
                settings.vPlanCourses
                        .split(",")
                        .map { it.trim() }
                        .filter { !it.isBlank() }
                        .toSet()
        )

    data class VPlanSettings(val showAll: Boolean, val grades: Set<String>, val courses: Set<String>)

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cache"
    }
}
