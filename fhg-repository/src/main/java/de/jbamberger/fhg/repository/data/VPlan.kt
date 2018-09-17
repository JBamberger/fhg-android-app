package de.jbamberger.fhg.repository.data

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlan constructor(val day1: VPlanDay, val day2: VPlanDay) {

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
        val subNr: String,
        val subTeacher: String,
        val subFrom: String,
        val subTo: String
)

data class VPlanHeader(
        val dateAndDay: String,
        val lastUpdated: String,
        val motd: String
)
