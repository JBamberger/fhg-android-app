package de.jbamberger.fhg.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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
                // TODO: add more advanece fetch logic
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

    override fun postsOfFeed(): Listing<Pair<FeedItem, FeedMedia?>> {
        val pageSize = 10
        val sourceFactory = FeedDataSource.Factory(endpoint, appExecutors.networkIO())
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize * 2)
                .setPageSize(pageSize)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(appExecutors.networkIO())
                .build()

        return Listing(
                pagedList = pagedList,
                networkState = sourceFactory.sourceLiveData.switchMap { it.networkState },
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
                refreshState = sourceFactory.sourceLiveData.switchMap { it.initialLoad }
        )
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}