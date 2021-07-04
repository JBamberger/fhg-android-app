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

