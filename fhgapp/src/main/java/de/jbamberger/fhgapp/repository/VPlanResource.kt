package de.jbamberger.fhgapp.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.jbamberger.fhgapp.repository.api.ApiResponse
import de.jbamberger.fhgapp.repository.api.UntisFhgEndpoint
import de.jbamberger.fhgapp.repository.api.UntisVPlanRequest
import de.jbamberger.fhgapp.repository.data.VPlan
import de.jbamberger.fhgapp.repository.db.KeyValueStorage
import de.jbamberger.fhgapp.repository.util.AppExecutors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A generic class that can provide a resource backed by both a database and the network.
 *
 * @param <Result> type of data that is passed to the user
 * @param <Request> type that is returned from the network calls
 */
internal class VPlanResource
@MainThread internal constructor(
    private val appExecutors: AppExecutors,
    private val kvStore: KeyValueStorage,
    private val untisEndpoint: UntisFhgEndpoint
) {

    private var shouldFetchFromNet = true

    private val result = MediatorLiveData<Resource<VPlan>>()

    init {
        result.value = Resource.Loading(null)
        val dbSource = loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            val fetch = shouldFetchFromNet
            shouldFetchFromNet = false
            if (fetch) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, this::handleDbResult)
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<VPlan>) {
        val apiResponse = MediatorLiveData<ApiResponse<VPlan>>()
        apiResponse.addSource(getVPlan(), apiResponse::setValue)
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { result.value = Resource.Loading(it) }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response!!.isSuccessful) {
                appExecutors.diskIO().execute {
                    kvStore.save(RepositoryImpl.VPLAN_KEY, response.body!!)
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb(), this::handleDbResult)
                    }
                }
            } else {
                shouldFetchFromNet = true
                result.addSource(dbSource) {
                    result.value = Resource.Error(
                        response.errorMessage
                            ?: "Failed to load resource", it
                    )
                }
            }
        }
    }

    private fun handleDbResult(value: VPlan?) {
        when (value) {
            null -> result.value = Resource.Error("db returned null", value)
            else -> result.value = Resource.Success(value)
        }
    }

    private fun loadFromDb(): LiveData<VPlan> {
        val l = MutableLiveData<VPlan>()
        l.value = kvStore.get(RepositoryImpl.VPLAN_KEY)
        return l
    }

    internal fun asLiveData(): LiveData<Resource<VPlan>> {
        return result
    }

    private fun getVPlan(): LiveData<ApiResponse<VPlan>> {
        val day1 = untisEndpoint.getVPlanDay(UntisVPlanRequest.today())
        val day2 = untisEndpoint.getVPlanDay(UntisVPlanRequest.tomorrow())


        val isLoaded = AtomicBoolean(false)
        val builder = VPlan.Builder()
        val merger = MediatorLiveData<ApiResponse<VPlan>>()
        merger.addSource(day1) { response ->
            merger.removeSource(day1)

            if (response != null && response.isSuccessful && response.body != null) {
                builder.addDay1(response.body)
                if (isLoaded.getAndSet(true)) {
                    merger.value = ApiResponse(builder.build(), response)
                }
            } else {
                merger.removeSource(day2)
                if (response != null) {
                    merger.setValue(ApiResponse(Throwable(response.errorMessage)))
                } else {
                    merger.setValue(ApiResponse(Throwable("Network error")))
                }
            }
        }
        merger.addSource(day2) { response ->
            merger.removeSource(day2)

            if (response != null && response.isSuccessful && response.body != null) {
                builder.addDay2(response.body)
                if (isLoaded.getAndSet(true)) {
                    merger.value = ApiResponse(builder.build(), response)
                }
            } else {
                merger.removeSource(day1)
                if (response != null) {
                    merger.setValue(ApiResponse(Throwable(response.errorMessage)))
                } else {
                    merger.setValue(ApiResponse(Throwable("Network error")))
                }
            }
        }
        return merger
    }
}
