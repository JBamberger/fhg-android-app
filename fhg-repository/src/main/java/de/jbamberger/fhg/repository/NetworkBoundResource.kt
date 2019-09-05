package de.jbamberger.fhg.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.jbamberger.fhg.repository.api.ApiResponse
import de.jbamberger.fhg.repository.util.AppExecutors

/**
 * A generic class that can provide a resource backed by both a database and the network.
 *
 * @param <Result> type of data that is passed to the user
 * @param <Request> type that is returned from the network calls
 */
internal class NetworkBoundResource<Result, Request> @MainThread internal constructor(
        private val appExecutors: AppExecutors, private val provider: Provider<Result, Request>) {

    private val result = MediatorLiveData<Resource<Result>>()

    init {
        result.value = Resource.Loading(null)
        val dbSource = provider.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            if (provider.shouldFetch(it)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, this::handleDbResult)
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<Result>) {
        val apiResponse = provider.createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { result.value = Resource.Loading(it) }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response!!.isSuccessful) {
                appExecutors.diskIO().execute {
                    provider.saveCallResult(provider.processResponse(response)!!)
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(provider.loadFromDb(), this::handleDbResult)
                    }
                }
            } else {
                provider.onFetchFailed()
                result.addSource(dbSource) {
                    result.value = Resource.Error(response.errorMessage!!, it)
                }
            }
        }
    }

    private fun handleDbResult(value: Result?) {
        when (value) {
            null -> result.value = Resource.Error("db returned null", value)
            else -> result.value = Resource.Success(value)
        }
    }

    internal fun asLiveData(): LiveData<Resource<Result>> {
        return result
    }

    internal interface Provider<ResultType, RequestType> {

        fun onFetchFailed() {}

        @WorkerThread
        fun processResponse(response: ApiResponse<RequestType>): RequestType? {
            return response.body
        }

        @WorkerThread
        fun saveCallResult(item: RequestType)

        @MainThread
        fun shouldFetch(data: ResultType?): Boolean

        @MainThread
        fun loadFromDb(): LiveData<ResultType>

        @MainThread
        fun createCall(): LiveData<ApiResponse<RequestType>>

    }
}
