package de.jbamberger.fhg.repository.api

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.jbamberger.fhg.repository.NetworkState
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhg.repository.data.VPlan
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

internal class FhgApiImpl @Inject constructor(private val endpoint: FhgEndpoint) : FhgApi {

    override fun getVPlan(): LiveData<ApiResponse<VPlan>> {
        val day1 = endpoint.getVPlanFrame1()
        val day2 = endpoint.getVPlanFrame2()
        val isLoaded = AtomicBoolean(false)
        val builder = VPlan.Builder()
        val merger = MediatorLiveData<ApiResponse<VPlan>>()
        merger.addSource(day1) { response ->
            merger.removeSource(day1)

            if (response != null && response.isSuccessful && response.body != null) {
                builder.addDay1(response.body)
                if (isLoaded.getAndSet(true)) {
                    merger.value = ApiResponse(builder.build(), response)
                }
            } else {
                merger.removeSource(day2)
                if (response != null) {
                    merger.setValue(ApiResponse(Throwable(response.errorMessage)))
                } else {
                    merger.setValue(ApiResponse(Throwable("Network error")))
                }
            }
        }
        merger.addSource(day2) { response ->
            merger.removeSource(day2)

            if (response != null && response.isSuccessful && response.body != null) {
                builder.addDay2(response.body)
                if (isLoaded.getAndSet(true)) {
                    merger.value = ApiResponse(builder.build(), response)
                }
            } else {
                merger.removeSource(day1)
                if (response != null) {
                    merger.setValue(ApiResponse(Throwable(response.errorMessage)))
                } else {
                    merger.setValue(ApiResponse(Throwable("Network error")))
                }
            }
        }
        return merger
    }

    override fun getFeed(count: Int, before: String?): Download<List<Pair<FeedItem, FeedMedia?>>> {
        return try {
            val response = when (before) {
                null -> endpoint.getFeedPage(count = count)
                else -> endpoint.getFeedPage(count = count, before = before)
            }.execute()

            if (response.isSuccessful) {
                Download.Success(response.body()?.map(::resolveMedia) ?: emptyList())
            } else {
                Download.Error("Feed download failed with response code ${response.code()}")
            }
        } catch (e: IOException) {
            Download.Error(e.message ?: "Feed download failed with unknown exception.")
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
}