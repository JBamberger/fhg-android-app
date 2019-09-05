package de.jbamberger.fhg.repository.api

import androidx.lifecycle.LiveData
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhg.repository.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgApi {
    fun getVPlan(): LiveData<ApiResponse<VPlan>>
    fun getFeed(count: Int, before: String?): Download<List<Pair<FeedItem, FeedMedia?>>>
}
