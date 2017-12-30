package de.jbamberger.api.data

import de.jbamberger.util.Preconditions.checkNotNull

class VPlanDay(dateAndDay: String, lastUpdated: String, motd: String, vPlanRows: List<VPlanRow>) {

    val dateAndDay: String

    val lastUpdated: String

    val motd: String

    val vPlanRows: List<VPlanRow>

    init {
        this.dateAndDay = checkNotNull(dateAndDay)
        this.lastUpdated = checkNotNull(lastUpdated)
        this.motd = checkNotNull(motd)
        this.vPlanRows = checkNotNull(vPlanRows)
    }

    override fun toString(): String {
        return "VPlanDay{" +
                "dateAndDay='" + dateAndDay + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", motd='" + motd + '\'' +
                ", vPlanRows=" + vPlanRows +
                '}'
    }
}
