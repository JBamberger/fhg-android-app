package de.jbamberger.fhgapp.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import de.jbamberger.fhg.repository.Repository
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedViewModel @Inject
internal constructor(private val repo: Repository) : ViewModel() {
    private val source = MutableLiveData<String>()
    private val repoResult = map(source) { repo.postsOfFeed() }

    val posts = switchMap(repoResult) { it.pagedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!

    init {
        source.postValue("")
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}