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
import de.jbamberger.fhgapp.repository.api.FeedDataSource
import de.jbamberger.fhgapp.repository.api.FhgEndpoint
import de.jbamberger.fhgapp.repository.api.UntisFhgEndpoint
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.repository.data.VPlan
import de.jbamberger.fhgapp.repository.db.KeyValueStorage
import de.jbamberger.fhgapp.repository.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject internal constructor(
    private val appExecutors: AppExecutors,
    private val endpoint: FhgEndpoint,
    private val untisEndpoint: UntisFhgEndpoint,
    private val kvStore: KeyValueStorage
) : Repository {

    override fun getVPlan(): LiveData<Resource<VPlan>> {
        return VPlanResource(appExecutors, kvStore, untisEndpoint).asLiveData()
    }

    override fun getFeed(): PagingSource<String, Pair<FeedItem, FeedMedia?>> {
        return FeedDataSource(endpoint)
    }

    companion object {
        const val VPLAN_KEY = "de.jbamberger.fhgapp.source.vplan_cachev2"
    }
}