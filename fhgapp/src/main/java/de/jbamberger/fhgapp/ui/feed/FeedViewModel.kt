/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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