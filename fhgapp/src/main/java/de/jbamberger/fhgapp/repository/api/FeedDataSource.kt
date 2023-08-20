/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.fhgapp.repository.api


import androidx.paging.PagingSource
import androidx.paging.PagingState
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import timber.log.Timber
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
            Timber.e(e, "Failed to load feed page.")
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
                Timber.e(e, "Could not resolve feed media element for item $item")
            }
        }
        return Pair(item, null)
    }
}

