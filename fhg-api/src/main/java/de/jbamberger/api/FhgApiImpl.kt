package de.jbamberger.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.api.data.VPlan
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class FhgApiImpl @Inject
constructor(context: Context) : FhgApi {

    private val endpoint: FhgEndpoint = NetModule.getEndpoint(context)

    override val vPlan: LiveData<ApiResponse<VPlan>>
        get() {
            val day1 = endpoint.vPlanFrame1
            val day2 = endpoint.vPlanFrame2
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

    override val feed: LiveData<ApiResponse<FeedChunk>>
        get() = endpoint.feed

}
