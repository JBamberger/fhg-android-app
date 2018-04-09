package de.jbamberger.fhg.repository.api

import android.arch.paging.PositionalDataSource
import de.jbamberger.fhg.repository.data.FeedItem

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class PagedFeedDataSource(val api: FhgEndpoint) : PositionalDataSource<List<FeedItem>>() {

    private var retry: (() -> Any)? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<List<FeedItem>>) {
        TODO("not implemented")
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<List<FeedItem>>) {
        val request = api.getFeedPaged(1)

        val response = request.execute()
        val data = response.body()


    }
}