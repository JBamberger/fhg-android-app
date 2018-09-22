package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import de.jbamberger.fhg.repository.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

internal class FeedDataSource private constructor(
        private val api: FhgEndpoint,
        private val retryExecutor: Executor) : ItemKeyedDataSource<String, FeedItem>() {

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
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }


    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<FeedItem>) {
        val request = api.getFeedPage(count = params.requestedLoadSize)
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()

            if (response.isSuccessful) {
                val items = response.body() ?: emptyList()
                items.forEach {

                }

                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(items)
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<FeedItem>) {
        networkState.postValue(NetworkState.LOADING)
        api.getFeedPage(
                before = params.key,
                count = params.requestedLoadSize).enqueue(
                object : Callback<List<FeedItem>> {
                    override fun onFailure(call: Call<List<FeedItem>>, t: Throwable) {
                        retry = { loadAfter(params, callback) }
                        networkState.postValue(NetworkState.ERROR(t.message ?: "unknown error"))
                    }

                    override fun onResponse(
                            call: Call<List<FeedItem>>, response: Response<List<FeedItem>>) {
                        if (response.isSuccessful) {
                            val items = response.body() ?: emptyList()
                            retry = null
                            callback.onResult(items)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = { loadAfter(params, callback) }
                            networkState.postValue(NetworkState.ERROR("error code: ${response.code()}"))
                        }
                    }
                }
        )
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<FeedItem>) {
        // ignore
    }

    override fun getKey(item: FeedItem): String = item.date!! //FIXME

    internal class Factory(
            private val api: FhgEndpoint,
            private val retryExecutor: Executor) : DataSource.Factory<String, FeedItem>() {

        val sourceLiveData = MutableLiveData<FeedDataSource>()

        override fun create(): DataSource<String, FeedItem> {
            val source = FeedDataSource(api, retryExecutor)
            sourceLiveData.postValue(source)
            return source
        }
    }
}

