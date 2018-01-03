package de.jbamberger.api.data

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

data class VPlanRow(
        val subject: String,
        val isOmitted: Boolean,
        val hour: String,
        val room: String,
        val content: String,
        val grade: String,
        val kind: String,
        val isMarkedNew: Boolean)