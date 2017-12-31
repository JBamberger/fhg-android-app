package de.jbamberger.fhgapp.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import de.jbamberger.api.ApiResponse
import de.jbamberger.api.FhgApi
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.api.data.VPlan
import de.jbamberger.fhgapp.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
class Repository @Inject
constructor(private val appExecutors: AppExecutors, private val api: FhgApi) {
    private var vPlanCache: VPlan? = null
    private var feedCache: FeedChunk? = null

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
                m.addSource(api.vPlan, { m.setValue(it) })
                return m
            }
        }.asLiveData()

    val feed: LiveData<Resource<FeedChunk>>
        get() = object : NetworkBoundResource<FeedChunk, FeedChunk>(appExecutors) {
            override fun saveCallResult(item: FeedChunk) {
                feedCache = item
            }

            override fun shouldFetch(data: FeedChunk?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<FeedChunk> {
                val l = MutableLiveData<FeedChunk>()
                l.value = feedCache
                return l
            }

            override fun createCall(): LiveData<ApiResponse<FeedChunk>> {
                val m = MediatorLiveData<ApiResponse<FeedChunk>>()
                m.addSource(api.feed, { m.setValue(it) })
                return m
            }
        }.asLiveData()

}
