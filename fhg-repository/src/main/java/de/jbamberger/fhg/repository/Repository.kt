package de.jbamberger.fhg.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import de.jbamberger.fhg.repository.api.ApiResponse
import de.jbamberger.fhg.repository.api.FeedDataSource
import de.jbamberger.fhg.repository.api.FhgApi
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhg.repository.db.KeyValueStorage
import de.jbamberger.fhg.repository.util.AppExecutors
import de.jbamberger.fhg.repository.util.switchMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface Repository {
    fun getVPlan(): LiveData<Resource<VPlan>>

    fun postsOfFeed(): Listing<Pair<FeedItem, FeedMedia?>>
}

@Singleton
internal class RepositoryImpl @Inject internal constructor(
        private val appExecutors: AppExecutors,
        private val api: FhgApi,
        private val endpoint: FhgEndpoint,
        private val kvStore: KeyValueStorage) : Repository {

    override fun getVPlan(): LiveData<Resource<VPlan>> {
        val provider = object : NetworkBoundResource.Provider<VPlan, VPlan> {
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
                m.addSource(api.getVPlan(), m::setValue)
                return m
            }
        }
        return NetworkBoundResource(appExecutors, provider).asLiveData()
    }

    override fun postsOfFeed() = postsOfFeed(10)

    @MainThread
    fun postsOfFeed(pageSize: Int): Listing<Pair<FeedItem, FeedMedia?>> {
        val sourceFactory = FeedDataSource.Factory(endpoint, appExecutors.networkIO())
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize * 2)
                .setPageSize(pageSize)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(appExecutors.networkIO())
                .build()

        val refreshState = sourceFactory.sourceLiveData.switchMap { it.initialLoad }

        return Listing(
                pagedList = pagedList,
                networkState = sourceFactory.sourceLiveData.switchMap { it.networkState },
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
                refreshState = refreshState
        )
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}
