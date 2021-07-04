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

package de.jbamberger.fhgapp.ui.about.license

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhgapp.repository.util.AppExecutors
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LicenseViewModel
@Inject internal constructor(
    app: Application,
    executors: AppExecutors,
    dependencyReader: DependencyReader
) :
    AndroidViewModel(app) {

    private val _dependencies = MutableLiveData<List<OssDependencyListItem>>()

    val dependencies: LiveData<List<OssDependencyListItem>>
        get() = _dependencies

    init {
        executors.diskIO().execute {
            try {
                val dependencyList = dependencyReader.getDependencies()
                    .flatMap { entry ->
                        val (group, libs) = entry

                        val licenseString = if (group != DependencyGroup.OTHERS)
                            getLicenseString(libs.flatMap { it.licenses }.toSet()) else null

                        val groupList = mutableListOf<OssDependencyListItem>(
                            OssDependencyListItem.Header(group.displayName, licenseString)
                        )

                        libs.mapTo(groupList) {
                            if (licenseString != null) {
                                OssDependencyListItem.Library(it.name)
                            } else {
                                OssDependencyListItem.Library(it.name, getLicenseString(it.licenses))
                            }
                        }

                        groupList
                    }

                executors.mainThread().execute {
                    publishOssDependencyList(dependencyList)
                }
            } catch (e: DependencyReadingException) {
                Timber.e(e, "Failed to load dependency list.")
                throw NotImplementedError(
                    e.message ?: "Failed to load dep. list. Error message missing."
                )
            }
        }
    }

    private fun getLicenseString(licenses: Set<DependencyLicense>) =
        licenses.joinToString(separator = "\n") { license ->
            license.url?.let { "${license.name}: $it" } ?: license.name
        }

    private fun publishOssDependencyList(deps: List<OssDependencyListItem>) {
        _dependencies.value = deps
    }
}