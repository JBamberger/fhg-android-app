package de.jbamberger.fhg.repository.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.VPlan
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class FhgApiImpl @Inject
constructor(private val endpoint: FhgEndpoint) : FhgApi {

    override fun getVPlan(): LiveData<ApiResponse<VPlan>>  {
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

    override fun getFeed(): LiveData<ApiResponse<List<FeedItem>>> = endpoint.getFeed()
}
