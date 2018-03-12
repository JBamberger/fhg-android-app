package de.jbamberger.fhgapp.ui.feed

import android.arch.lifecycle.ViewModel
import de.jbamberger.fhg.repository.Repository
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    internal var feed = repo.feed

    internal fun refresh() {
        feed = repo.feed
    }
}