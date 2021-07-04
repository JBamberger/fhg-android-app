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

import de.jbamberger.fhgapp.R

sealed interface OssDependencyListItem {

    val layoutId: Int

    data class Header(
        val name: String,
        val licenseString: String? = null
    ) : OssDependencyListItem {
        override val layoutId get() = R.layout.oss_list_header
    }

    data class Library(
        val name: String,
        val licenseString: String? = null
    ) : OssDependencyListItem {
        override val layoutId get() = R.layout.oss_list_item
    }
}
