package de.jbamberger.fhg.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import dagger.Lazy
import de.jbamberger.fhg.repository.api.ApiResponse
import de.jbamberger.fhg.repository.api.FeedDataSource
import de.jbamberger.fhg.repository.api.FhgApi
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhg.repository.db.AppDatabase
import de.jbamberger.fhg.repository.db.KeyValueStorage
import de.jbamberger.fhg.repository.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface Repository {
    fun getVPlan(): LiveData<Resource<VPlan>>

    fun getFeed(): LiveData<Resource<List<FeedItem>>>

    fun postsOfFeed(): Listing<FeedItem>
}

@Singleton
class RepositoryImpl @Inject internal constructor(
        appExecutors: Lazy<AppExecutors>,
        api: Lazy<FhgApi>,
        endpoint: Lazy<FhgEndpoint>,
        db: Lazy<AppDatabase>,
        kvStore: Lazy<KeyValueStorage>) : Repository {

    private val appExecutors: AppExecutors by lazy { appExecutors.get() }
    private val api: FhgApi  by lazy { api.get() }
    private val endpoint: FhgEndpoint  by lazy { endpoint.get() }
    private val db: AppDatabase by lazy { db.get() }
    private val kvStore: KeyValueStorage  by lazy { kvStore.get() }

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

    override fun getFeed(): LiveData<Resource<List<FeedItem>>> {
        val provider = object : NetworkBoundResource.Provider<List<FeedItem>, List<FeedItem>> {
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
                m.addSource(api.getFeed(), m::setValue)
                return m
            }
        }
        return NetworkBoundResource(appExecutors, provider).asLiveData()
    }

    override fun postsOfFeed() = postsOfFeed(10)

    @MainThread
    fun postsOfFeed(pageSize: Int): Listing<FeedItem> {
        val sourceFactory = FeedDataSource.Factory(endpoint, appExecutors.networkIO())
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize * 2)
                .setPageSize(pageSize)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(appExecutors.networkIO())
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }

        return Listing(
                pagedList = pagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.networkState },
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
                refreshState = refreshState
        )
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}
