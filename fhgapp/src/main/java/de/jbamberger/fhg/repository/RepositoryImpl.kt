package de.jbamberger.fhg.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import de.jbamberger.fhg.repository.api.FeedDataSource
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.api.UntisFhgEndpoint
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
    private val endpoint: FhgEndpoint,
    private val untisEndpoint: UntisFhgEndpoint,
    private val kvStore: KeyValueStorage
) : Repository {

    override fun getVPlan(): LiveData<Resource<VPlan>> {

        return VPlanResource(appExecutors, kvStore, endpoint, untisEndpoint).asLiveData()
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
            refreshState = sourceFactory.sourceLiveData.switchMap { it.initialLoad },
            retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
            refresh = { sourceFactory.sourceLiveData.value?.invalidate() }
        )
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}