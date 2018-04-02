package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.LiveData
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgApi {
    val vPlan: LiveData<ApiResponse<VPlan>>
    val feed: LiveData<ApiResponse<List<FeedItem>>>
}
