package de.jbamberger.fhg.repository.api


import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import de.jbamberger.fhg.repository.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import java.io.IOException
import java.util.concurrent.Executor

internal class FeedDataSource private constructor(
        private val api: FhgApi,
        private val retryExecutor: Executor) : ItemKeyedDataSource<String, Pair<FeedItem, FeedMedia?>>() {

    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let { retryExecutor.execute { it.invoke() } }
    }


    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Pair<FeedItem, FeedMedia?>>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        val result = when (val response = api.getFeed(params.requestedLoadSize, null)) {
            is Download.Success -> {
                retry = null
                callback.onResult(response.data)
                NetworkState.LOADED
            }
            is Download.Error -> {
                retry = { loadInitial(params, callback) }
                NetworkState.ERROR(response.message)
            }
        }

        networkState.postValue(result)
        initialLoad.postValue(result)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Pair<FeedItem, FeedMedia?>>) {
        networkState.postValue(NetworkState.LOADING)

        val result = when (val response = api.getFeed(params.requestedLoadSize, params.key)) {
            is Download.Success -> {
                retry = null
                callback.onResult(response.data)
                NetworkState.LOADED
            }
            is Download.Error -> {
                retry = { loadAfter(params, callback) }
                NetworkState.ERROR(response.message)
            }
        }

        networkState.postValue(result)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Pair<FeedItem, FeedMedia?>>) {
        // ignore
    }

    override fun getKey(pair: Pair<FeedItem, FeedMedia?>): String = pair.first.date!! //FIXME

    internal class Factory(
            private val api: FhgApi,
            private val retryExecutor: Executor) : DataSource.Factory<String, Pair<FeedItem, FeedMedia?>>() {

        val sourceLiveData = MutableLiveData<FeedDataSource>()

        override fun create(): DataSource<String, Pair<FeedItem, FeedMedia?>> {
            val source = FeedDataSource(api, retryExecutor)
            sourceLiveData.postValue(source)
            return source
        }
    }
}

