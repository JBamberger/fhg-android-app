package de.jbamberger.fhgapp.repository.api


import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import de.jbamberger.fhgapp.repository.NetworkState
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import java.io.IOException
import java.util.concurrent.Executor

internal class FeedDataSource private constructor(
    private val endpoint: FhgEndpoint,
    private val retryExecutor: Executor
) : ItemKeyedDataSource<String, Pair<FeedItem, FeedMedia?>>() {

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


    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Pair<FeedItem, FeedMedia?>>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        val result = when (val response = getFeed(params.requestedLoadSize, null)) {
            is FeedDownload.Success -> {
                retry = null
                callback.onResult(response.data)
                NetworkState.LOADED
            }
            is FeedDownload.Error -> {
                retry = { loadInitial(params, callback) }
                NetworkState.ERROR(response.message)
            }
        }

        networkState.postValue(result)
        initialLoad.postValue(result)
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<Pair<FeedItem, FeedMedia?>>
    ) {
        networkState.postValue(NetworkState.LOADING)

        val result = when (val response = getFeed(params.requestedLoadSize, params.key)) {
            is FeedDownload.Success -> {
                retry = null
                callback.onResult(response.data)
                NetworkState.LOADED
            }
            is FeedDownload.Error -> {
                retry = { loadAfter(params, callback) }
                NetworkState.ERROR(response.message)
            }
        }

        networkState.postValue(result)
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<Pair<FeedItem, FeedMedia?>>
    ) {
        // ignore
    }

    override fun getKey(pair: Pair<FeedItem, FeedMedia?>): String = pair.first.date!! //FIXME


    private fun getFeed(
        count: Int,
        before: String?
    ): FeedDownload<List<Pair<FeedItem, FeedMedia?>>> {
        return try {
            val response = when (before) {
                null -> endpoint.getFeedPage(count = count)
                else -> endpoint.getFeedPage(count = count, before = before)
            }.execute()

            if (response.isSuccessful) {
                FeedDownload.Success(response.body()?.map(::resolveMedia) ?: emptyList())
            } else {
                FeedDownload.Error("Feed download failed with response code ${response.code()}")
            }
        } catch (e: IOException) {
            FeedDownload.Error(e.message ?: "Feed download failed with unknown exception.")
        }
    }

    @WorkerThread
    private fun resolveMedia(item: FeedItem): Pair<FeedItem, FeedMedia?> {
        val mediaId = item.featuredMedia
        if (mediaId != null && mediaId > 0) {
            try {
                val response = endpoint.getFeedMedia(mediaId).execute()
                if (response.isSuccessful) {
                    return Pair(item, response.body())
                }
            } catch (e: IOException) {
            }
        }
        return Pair(item, null)
    }


    internal class Factory(
        private val endpoint: FhgEndpoint,
        private val retryExecutor: Executor
    ) : DataSource.Factory<String, Pair<FeedItem, FeedMedia?>>() {

        val sourceLiveData = MutableLiveData<FeedDataSource>()

        override fun create(): DataSource<String, Pair<FeedItem, FeedMedia?>> {
            val source = FeedDataSource(endpoint, retryExecutor)
            sourceLiveData.postValue(source)
            return source
        }
    }
}

