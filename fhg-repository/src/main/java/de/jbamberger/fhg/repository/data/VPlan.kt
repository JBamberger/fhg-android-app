package de.jbamberger.fhg.repository.data

import com.squareup.moshi.JsonClass

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@JsonClass(generateAdapter = true)
data class VPlan constructor(val day1: VPlanDay, val day2: VPlanDay) {

    class Builder {
        private var day1: VPlanDay? = null
        private var day2: VPlanDay? = null

        @Synchronized
        fun addDay1(day1: VPlanDay): Builder {
            this.day1 = day1
            return this
        }

        @Synchronized
        fun addDay2(day2: VPlanDay): Builder {
            this.day2 = day2
            return this
        }

        @Synchronized
        fun build(): VPlan {
            return VPlan(day1!!, day2!!)
        }
    }
}

@JsonClass(generateAdapter = true)
data class VPlanDay(
        val header: VPlanHeader,
        val vPlanRows: List<VPlanRow>
)

@JsonClass(generateAdapter = true)
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

@JsonClass(generateAdapter = true)
data class VPlanHeader(
        val dateAndDay: String,
        val lastUpdated: String,
        val motd: String
)
