package de.jbamberger.fhgapp.ui.vplan

import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import de.jbamberger.fhgapp.R

sealed class VPlanListItem {
    abstract fun getLayoutId(): Int
    abstract fun getData(): Any?

    data class Row(val data: VPlanRow) : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.vplan_item_variant
        override fun getData(): Any? = data
    }

    data class Header(val data: VPlanHeader) : VPlanListItem() {
        override fun getLayoutId(): Int = R.layout.vplan_header
        override fun getData(): Any? = data
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