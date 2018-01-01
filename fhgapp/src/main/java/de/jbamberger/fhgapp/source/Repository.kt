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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
class Repository @Inject
constructor(private val appExecutors: AppExecutors, private val api: FhgApi, private val db: AppDatabase) {
    private var vPlanCache: VPlan? = null
    private var feedFromNet: Boolean = true

    val vPlan: LiveData<Resource<VPlan>>
        get() = object : NetworkBoundResource<VPlan, VPlan>(appExecutors) {
            override fun saveCallResult(item: VPlan) {
                vPlanCache = item
            }

            override fun shouldFetch(data: VPlan?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<VPlan> {
                val l = MutableLiveData<VPlan>()
                l.value = vPlanCache
                return l
            }

            override fun createCall(): LiveData<ApiResponse<VPlan>> {
                val m = MediatorLiveData<ApiResponse<VPlan>>()
                m.addSource(api.vPlan, m::setValue)
                return m
            }
        }.asLiveData()

    val feed: LiveData<Resource<List<FeedItem>>>
        get() = object : NetworkBoundResource<List<FeedItem>, List<FeedItem>>(appExecutors) {
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
        }.asLiveData()

}
