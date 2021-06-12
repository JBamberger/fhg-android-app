package de.jbamberger.fhgapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhgapp.repository.Repository
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@HiltViewModel
class FeedViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    private val pageSize = 10
    private val config = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = false,
        initialLoadSize = pageSize * 2
    )

    private val pager = Pager(
        config = config,
        pagingSourceFactory = repo::getFeed
    )

    val feed = pager.flow.cachedIn(viewModelScope)
}