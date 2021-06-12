package de.jbamberger.fhgapp.repository

import androidx.lifecycle.LiveData
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.repository.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface Repository {
    fun getVPlan(): LiveData<Resource<VPlan>>

    fun postsOfFeed(): Listing<Pair<FeedItem, FeedMedia?>>
}

