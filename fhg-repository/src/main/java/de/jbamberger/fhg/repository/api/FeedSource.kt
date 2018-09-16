package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.paging.DataSource
import android.arch.paging.ItemKeyedDataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.util.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

sealed class NetworkState {
    object LOADED : NetworkState()
    object LOADING : NetworkState()
    data class ERROR(val message: String) : NetworkState()
}

internal class KeyedFeedDataSource(
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
}

internal class FeedDataSourceFactory(
        private val api: FhgEndpoint,
        private val retryExecutor: Executor) : DataSource.Factory<String, FeedItem>() {

    val sourceLiveData = MutableLiveData<KeyedFeedDataSource>()
    override fun create(): DataSource<String, FeedItem> {
        val source = KeyedFeedDataSource(api, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}

@Singleton
internal class FeedDataRepository
@Inject constructor(
        private val api: FhgEndpoint,
        executors: AppExecutors) {

    private val networkExecutor = executors.networkIO()

    @MainThread
    fun postsOfFeed(pageSize: Int): Listing<FeedItem> {
        val sourceFactory = FeedDataSourceFactory(api, networkExecutor)
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize * 2)
                .setPageSize(pageSize)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .build()

        val refreshState = switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }

        return Listing(
                pagedList = pagedList,
                networkState = switchMap(sourceFactory.sourceLiveData) { it.networkState },
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                refresh = { sourceFactory.sourceLiveData.value?.invalidate() },
                refreshState = refreshState
        )
    }
}

data class Listing<T>(
        // the LiveData of paged lists for the UI to observe
        val pagedList: LiveData<PagedList<T>>,
        // represents the network request status to show to the user
        val networkState: LiveData<NetworkState>,
        // represents the refresh status to show to the user. Separate from networkState, this
        // value is importantly only when refresh is requested.
        val refreshState: LiveData<NetworkState>,
        // refreshes the whole data and fetches it from scratch.
        val refresh: () -> Unit,
        // retries any failed requests.
        val retry: () -> Unit)