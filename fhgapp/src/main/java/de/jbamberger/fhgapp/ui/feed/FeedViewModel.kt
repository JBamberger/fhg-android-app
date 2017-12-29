package de.jbamberger.fhgapp.ui.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.fhgapp.source.Repository
import de.jbamberger.fhgapp.source.Resource
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    internal var feed: LiveData<Resource<FeedChunk>>? = null
        private set


    internal fun init() {
        if (this.feed != null) {
            return
        }
        feed = repo.feed
    }

    internal fun refresh() {
        feed = repo.feed
    }
}