package de.jbamberger.api.data

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanRow(val subject: String, val isOmitted: Boolean, val hour: String, val room: String, val content: String, val grade: String, val kind: String, val isMarkedNew: Boolean) {

    override fun toString(): String {
        return "VPlanRow{" +
                "subject='" + subject + '\'' +
                ", omitted=" + isOmitted +
                ", hour='" + hour + '\'' +
                ", room='" + room + '\'' +
                ", content='" + content + '\'' +
                ", grade='" + grade + '\'' +
                ", kind='" + kind + '\'' +
                ", markedNew=" + isMarkedNew +
                '}'
    }
}