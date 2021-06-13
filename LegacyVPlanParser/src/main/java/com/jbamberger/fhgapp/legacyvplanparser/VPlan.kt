package com.jbamberger.fhgapp.legacyvplanparser

data class VPlanDay(
    val header: VPlanHeader,
    val vPlanRows: List<VPlanRow>
)

data class VPlanRow(
    val subject: String,
    val isOmitted: Boolean,
    val hour: String,
    val room: String,
    val content: String,
    val grade: String,
    val kind: String,
    val isMarkedNew: Boolean,
    val subNr: String? = null,
    val subTeacher: String? = null,
    val subFrom: String? = null,
    val subTo: String? = null
)

data class VPlanHeader(
    val dateAndDay: String,
    val lastUpdated: String,
    val motd: String
)
