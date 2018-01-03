package de.jbamberger.api.data

import de.jbamberger.util.Preconditions.checkNotNull

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlan private constructor(val day1: VPlanDay, val day2: VPlanDay) {

    class Builder {

        private var day1: VPlanDay? = null
        private var day2: VPlanDay? = null

        @Synchronized
        fun addDay1(day1: VPlanDay): VPlan.Builder {
            this.day1 = day1
            return this
        }

        @Synchronized
        fun addDay2(day2: VPlanDay): VPlan.Builder {
            this.day2 = day2
            return this
        }

        @Synchronized
        fun build(): VPlan {
            checkNotNull<VPlanDay>(day1)
            checkNotNull<VPlanDay>(day2)
            return VPlan(day1!!, day2!!)
        }
    }
}
