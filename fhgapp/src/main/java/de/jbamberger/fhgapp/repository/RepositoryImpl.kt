package de.jbamberger.fhgapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import de.jbamberger.fhgapp.repository.api.FeedDataSource
import de.jbamberger.fhgapp.repository.api.FhgEndpoint
import de.jbamberger.fhgapp.repository.api.UntisFhgEndpoint
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.repository.data.VPlan
import de.jbamberger.fhgapp.repository.db.KeyValueStorage
import de.jbamberger.fhgapp.repository.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject internal constructor(
    private val appExecutors: AppExecutors,
    private val endpoint: FhgEndpoint,
    private val untisEndpoint: UntisFhgEndpoint,
    private val kvStore: KeyValueStorage
) : Repository {

    override fun getVPlan(): LiveData<Resource<VPlan>> {

        return VPlanResource(appExecutors, kvStore, endpoint, untisEndpoint).asLiveData()
    }

    override fun getFeed(): PagingSource<String, Pair<FeedItem, FeedMedia?>> {
        return FeedDataSource(endpoint)
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}