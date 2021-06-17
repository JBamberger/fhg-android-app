package de.jbamberger.fhgapp.repository.api


import androidx.paging.PagingSource
import androidx.paging.PagingState
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import java.io.IOException

internal class FeedDataSource internal constructor(
    private val endpoint: FhgEndpoint
) : PagingSource<String, Pair<FeedItem, FeedMedia?>>() {

    override fun getRefreshKey(state: PagingState<String, Pair<FeedItem, FeedMedia?>>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Pair<FeedItem, FeedMedia?>> {
        return try {
            val feed = endpoint.getFeedPage2(count = params.loadSize, before = params.key).map {
                resolveMedia(it)
            }

            val nextKey = if (feed.isNotEmpty()) feed[feed.size - 1].first.date else null
            LoadResult.Page(data = feed, prevKey = null, nextKey = nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    private suspend fun resolveMedia(item: FeedItem): Pair<FeedItem, FeedMedia?> {
        val mediaId = item.featuredMedia
        if (mediaId != null && mediaId > 0) {
            try {
                val media = endpoint.getFeedMedia2(mediaId)
                return Pair(item, media)
            } catch (e: IOException) {
            }
        }
        return Pair(item, null)
    }
}

