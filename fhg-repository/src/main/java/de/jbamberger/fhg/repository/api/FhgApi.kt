package de.jbamberger.fhg.repository.api

import androidx.lifecycle.LiveData
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgApi {
    fun getVPlan(): LiveData<ApiResponse<VPlan>>
    fun getFeed(): LiveData<ApiResponse<List<FeedItem>>>
}
