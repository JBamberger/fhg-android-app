package de.jbamberger.api

import android.arch.lifecycle.LiveData

import de.jbamberger.api.data.FeedChunk
import de.jbamberger.api.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface FhgApi {

    val vPlan: LiveData<ApiResponse<VPlan>>

    val feed: LiveData<ApiResponse<FeedChunk>>
}
