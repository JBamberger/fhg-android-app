package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.LiveData
import de.jbamberger.api.data.FeedItem
import de.jbamberger.api.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgApi {
    val vPlan: LiveData<ApiResponse<VPlan>>
    val feed: LiveData<ApiResponse<List<FeedItem>>>
}
