package de.jbamberger.fhg.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import de.jbamberger.fhg.repository.api.ApiResponse
import de.jbamberger.fhg.repository.util.AppExecutors

/**
 * A generic class that can provide a resource backed by both a database and the network.
 *
 * @param <ResultType> type of data that is passed to the user
 * @param <RequestType> type that is returned from the network calls
 */
class NetworkBoundResource<ResultType, RequestType>
@MainThread
internal constructor(private val appExecutors: AppExecutors,
                     private val provider: Provider<ResultType, RequestType>) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.Loading(null)
        val dbSource = provider.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            if (provider.shouldFetch(it)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) {
                    if (it == null) {
                        result.value = Resource.Error("db returned null", it)
                    } else {
                        result.value = Resource.Success(it)
                    }
                }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = provider.createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { result.setValue(Resource.Loading(it)) }
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
                        result.addSource(provider.loadFromDb()) {
                            if (it == null) {
                                result.value = Resource.Error("db returned null", it)
                            } else {
                                result.value = Resource.Success(it)
                            }
                        }
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

    fun asLiveData(): LiveData<Resource<ResultType>> {
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
