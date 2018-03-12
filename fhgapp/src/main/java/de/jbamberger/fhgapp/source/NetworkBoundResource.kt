package de.jbamberger.fhgapp.source

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread

import de.jbamberger.fhg.repository.ApiResponse
import de.jbamberger.fhgapp.AppExecutors

/**
 * A generic class that can provide a resource backed by both a database and the network.
 *
 * @param <ResultType> type of data that is passed to the user
 * @param <RequestType> type that is returned from the network calls
 */
class NetworkBoundResource<ResultType, RequestType>
@MainThread
constructor(private val appExecutors: AppExecutors,
                      private val provider: Provider<ResultType, RequestType>) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val dbSource = provider.loadFromDb()
        result.addSource(dbSource) {
            result.removeSource(dbSource)
            if (provider.shouldFetch(it)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { result.setValue(Resource.success(it)) }
            }
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = provider.createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { result.setValue(Resource.loading(it)) }
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
                            result.setValue(Resource.success(it))
                        }
                    }
                }
            } else {
                provider.onFetchFailed()
                result.addSource(dbSource) {
                    result.setValue(Resource.error(response.errorMessage!!, it))
                }
            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    interface Provider<ResultType, RequestType> {

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
