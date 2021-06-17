package de.jbamberger.fhgapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.repository.data.VPlan

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface Repository {
    fun getVPlan(): LiveData<Resource<VPlan>>

    fun getFeed(): PagingSource<String, Pair<FeedItem, FeedMedia?>>
}

