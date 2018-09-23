package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import android.support.annotation.WorkerThread
import de.jbamberger.fhg.repository.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import java.io.IOException
import java.util.concurrent.Executor

internal class FeedDataSource private constructor(
        private val api: FhgEndpoint,
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
        val request = api.getFeedPage(count = params.requestedLoadSize)
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()

            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                retry = null
                callback.onResult(items.map(::resolveMedia))
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
            } else {
                retry = { loadInitial(params, callback) }
                val error = NetworkState.ERROR("error code: ${response.code()}")
                networkState.postValue(error)
                initialLoad.postValue(error)
            }
        } catch (e: IOException) {
            retry = { loadInitial(params, callback) }
            val error = NetworkState.ERROR(e.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Pair<FeedItem, FeedMedia?>>) {
        networkState.postValue(NetworkState.LOADING)
        val request = api.getFeedPage(before = params.key, count = params.requestedLoadSize)

        try {
            val response = request.execute()
            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                retry = null
                callback.onResult(items.map(::resolveMedia))
                networkState.postValue(NetworkState.LOADED)
            } else {
                retry = { loadAfter(params, callback) }
                networkState.postValue(NetworkState.ERROR("error code: ${response.code()}"))
            }
        } catch (e: IOException) {
            retry = { loadAfter(params, callback) }
            networkState.postValue(NetworkState.ERROR(e.message ?: "unknown error"))
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Pair<FeedItem, FeedMedia?>>) {
        // ignore
    }

    override fun getKey(pair: Pair<FeedItem, FeedMedia?>): String = pair.first.date!! //FIXME

    @WorkerThread
    private fun resolveMedia(item: FeedItem): Pair<FeedItem, FeedMedia?> {
        val mediaId = item.featuredMedia
        if (mediaId != null && mediaId > 0) {
            try {
                val response = api.getFeedMedia(mediaId).execute()
                if (response.isSuccessful) {
                    return Pair(item, response.body())
                }
            } catch (e: IOException) {
            }
            return Pair(item, null)
        } else {
            return Pair(item, null)
        }
    }

    internal class Factory(
            private val api: FhgEndpoint,
            private val retryExecutor: Executor) : DataSource.Factory<String, Pair<FeedItem, FeedMedia?>>() {

        val sourceLiveData = MutableLiveData<FeedDataSource>()

        override fun create(): DataSource<String, Pair<FeedItem, FeedMedia?>> {
            val source = FeedDataSource(api, retryExecutor)
            sourceLiveData.postValue(source)
            return source
        }
    }
}

