package de.jbamberger.api.data

data class VPlanDay(
        val dateAndDay: String,
        val lastUpdated: String,
        val motd: String,
        val vPlanRows: List<VPlanRow>)
