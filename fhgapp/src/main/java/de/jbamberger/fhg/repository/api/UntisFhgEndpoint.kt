package de.jbamberger.fhg.repository.api

import androidx.lifecycle.LiveData
import de.jbamberger.fhg.repository.data.VPlanDay
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface UntisFhgEndpoint {
    companion object {
        internal const val BASE_URL = "https://hepta.webuntis.com"
    }

    @Headers("Content-Type: application/json")
    @POST("/WebUntis/monitor/substitution/data?school=FHG%20Radolfzell")
    fun getVPlanDay(@Body body: UntisVPlanRequest): LiveData<ApiResponse<VPlanDay>>
}
