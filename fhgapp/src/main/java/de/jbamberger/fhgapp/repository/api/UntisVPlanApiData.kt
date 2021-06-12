package de.jbamberger.fhgapp.repository.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
class UntisResponse<Payload> {
    var error: UntisError? = null
    var payload: Payload? = null
}

@JsonClass(generateAdapter = true)
class UntisError {
    @Json(name = "data")
    var data: Any? = null

    @Json(name = "code")
    var code: Int? = null

    @Json(name = "message")
    var message: String? = null
}

@JsonClass(generateAdapter = true)
class UntisVPlanDay {

    @Json(name = "date")
    var date: Int = 0

    @Json(name = "nextDate")
    var nextDate: Int = 0


    @Json(name = "rows")
    var rows: List<UntisVPlanRow> = emptyList()

    @Json(name = "lastUpdated")
    var lastUpdate: String? = ""


    @Json(name = "messageData")
    var messageData: UntisMessageData? = null

    @Json(name = "weekDay")
    var weekDay: String? = ""

//    var showingNextDate : Boolean = false
//    var absentElements : String = null
//    var affectedElements : String = null
//    var regularFreeData : String = null

}

@JsonClass(generateAdapter = true)
class UntisMessageData {
    @Json(name = "messages")
    var messages: List<UntisVPlanMessage> = emptyList()
}

@JsonClass(generateAdapter = true)
class UntisVPlanMessage {

    @Json(name = "subject")
    var subject: String? = null

    @Json(name = "body")
    var body: String? = null
}

@JsonClass(generateAdapter = true)
class UntisVPlanRow {
    /**
     * Fields are:
     *  - hour (Stunde): e.g.: 1 - 2
     *  - time (Zeit): e.g.: 07:45-09:20
     *  - class (Klassen): e.g.: 10b
     *  - subject (Fach): e.g.: E
     *  - room (Raum): e.g.: 223
     *  - info (Info): e.g.: "Verlegung nach 11.6. / 10:20"
     *  - substitution_text (Vertretungstext): e.g.: "verlegt auf Freitag"
     */
    @Json(name = "data")
    var fields: List<String> = emptyList()


    val hour: String get() = if (fields.size == SIZE) fields[0] else ""
    val time: String get() = if (fields.size == SIZE) fields[1] else ""
    val grade: String get() = if (fields.size == SIZE) fields[2] else ""
    val subject: String get() = if (fields.size == SIZE) fields[3] else ""
    val room: String get() = if (fields.size == SIZE) fields[4] else ""
    val teacher: String get() = if (fields.size == SIZE) fields[5] else ""
    val info: String get() = if (fields.size == SIZE) fields[6] else ""
    val substText: String get() = if (fields.size == SIZE) fields[7] else ""

    companion object {
        const val SIZE = 8
    }

}


@JsonClass(generateAdapter = true)
class UntisVPlanTicker {
    @Json(name = "tickerData")
    var tickerData: List<String> = emptyList()
}


@JsonClass(generateAdapter = true)
data class UntisVPlanRequest(
    @Json(name = "date")
    val date: Int,
    @Json(name = "dateOffset")
    val dateOffset: Int,


    @Json(name = "formatName")
    val formatName: String? = "VP_Internet",

    @Json(name = "schoolName")
    val schoolName: String? = "FHG Radolfzell",

    @Json(name = "strikethrough")
    val strikethrough: Boolean? = false,

    @Json(name = "mergeBlocks")
    val mergeBlocks: Boolean? = true,

    @Json(name = "showOnlyFutureSub")
    val showOnlyFutureSub: Boolean? = true,

    @Json(name = "showBreakSupervisions")
    val showBreakSupervisions: Boolean? = false,

    @Json(name = "showTeacher")
    val showTeacher: Boolean? = true,

    @Json(name = "showClass")
    val showClass: Boolean? = true,

    @Json(name = "showHour")
    val showHour: Boolean? = true,

    @Json(name = "showInfo")
    val showInfo: Boolean? = true,

    @Json(name = "showRoom")
    val showRoom: Boolean? = true,

    @Json(name = "showSubject")
    val showSubject: Boolean? = true,

    @Json(name = "groupBy")
    val groupBy: Int? = 1,

    @Json(name = "hideAbsent")
    val hideAbsent: Boolean? = false,

    @Json(name = "departmentIds")
    val departmentIds: List<Int>? = emptyList(),

    @Json(name = "departmentElementType")
    val departmentElementType: Int? = -1,

    @Json(name = "hideCancelWithSubstitution")
    val hideCancelWithSubstitution: Boolean? = false,

    @Json(name = "hideCancelCausedByEvent")
    val hideCancelCausedByEvent: Boolean? = false,

    @Json(name = "showTime")
    val showTime: Boolean? = true,

    @Json(name = "showSubstText")
    val showSubstText: Boolean? = true,

    @Json(name = "showAbsentElements")
    val showAbsentElements: List<Int>? = emptyList(),

    @Json(name = "showAffectedElements")
    val showAffectedElements: List<Int>? = emptyList(),

    @Json(name = "showUnitTime")
    val showUnitTime: Boolean? = false,

    @Json(name = "showMessages")
    val showMessages: Boolean? = true,

    @Json(name = "showStudentgroup")
    val showStudentgroup: Boolean? = false,

    @Json(name = "enableSubstitutionFrom")
    val enableSubstitutionFrom: Boolean? = false,

    @Json(name = "showSubstitutionFrom")
    val showSubstitutionFrom: Int? = 0,

    @Json(name = "showTeacherOnEvent")
    val showTeacherOnEvent: Boolean? = false,

    @Json(name = "showAbsentTeacher")
    val showAbsentTeacher: Boolean? = false,

    @Json(name = "strikethroughAbsentTeacher")
    val strikethroughAbsentTeacher: Boolean? = false,

    @Json(name = "activityTypeIds")
    val activityTypeIds: List<Int>? = listOf(4, 3, 2),

    @Json(name = "showEvent")
    val showEvent: Boolean? = false,

    @Json(name = "showCancel")
    val showCancel: Boolean? = true,

    @Json(name = "showOnlyCancel")
    val showOnlyCancel: Boolean? = false,

    @Json(name = "showSubstTypeColor")
    val showSubstTypeColor: Boolean? = false,

    @Json(name = "showExamSupervision")
    val showExamSupervision: Boolean? = false,

    @Json(name = "showUnheraldedExams")
    val showUnheraldedExams: Boolean? = true
) {
    companion object {
        private fun fromOffset(offset: Int): UntisVPlanRequest {
            val format = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
            val date = Date()
            return UntisVPlanRequest(format.format(date).toInt(), offset)
        }

        fun today(): UntisVPlanRequest = fromOffset(0)
        fun tomorrow(): UntisVPlanRequest = fromOffset(1)


    }
}
