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

package de.jbamberger.fhgapp.ui.vplan

import de.jbamberger.fhgapp.repository.data.VPlanHeader
import de.jbamberger.fhgapp.repository.data.VPlanRow
import de.jbamberger.fhgapp.R

sealed class VPlanListItem {
    abstract fun getLayoutId(): Int
    abstract fun getData(): Any?

    data class Row(val data: VPlanRow) : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.vplan_item_variant
        override fun getData(): Any = data
    }

    data class Header(val data: VPlanHeader) : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.vplan_header
        override fun getData(): Any = data
    }

    object Footer : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.vplan_footer
        override fun getData(): Any? = null
    }

    object Warning : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.list_flat_error
        override fun getData(): Any? = null
    }
}