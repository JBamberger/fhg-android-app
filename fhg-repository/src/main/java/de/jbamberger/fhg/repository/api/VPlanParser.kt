package de.jbamberger.fhg.repository.api

import android.support.annotation.VisibleForTesting
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.regex.Pattern

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
internal object VPlanParser {

    private const val GRADE_C = 0
    private const val HOUR_C = 1
    private const val SUBJECT_C = 2
    private const val ROOM_C = 3
    private const val KIND_C = 4
    private const val CONTENT_C = 5

    @Throws(ParseException::class)
    internal fun parseVPlanDay(body: ResponseBody): VPlanDay {
        return parseVPlanDay(readWithEncoding(body.bytes(), body.contentType()))
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun parseVPlanDay(html: String): VPlanDay {
        val doc = Jsoup.parse(html)
        return VPlanDay(VPlanHeader(readDayAndDate(doc), readLastUpdated(html), readMotdTable(doc)),
                readVPlanTable(doc))
    }

    @VisibleForTesting
    internal fun readWithEncoding(data: ByteArray, type: MediaType?): String {
        type?.charset().let {
            if (it != null) return String(data, it)
        }

        val dataDefaultEncoded = String(data)
        val matcher = Pattern
                .compile("<meta\\s+http-equiv=\"Content-Type\"\\s+content=\"([^\"]*)\"\\s*/?>")
                .matcher(dataDefaultEncoded)

        while (matcher.find()) {
            MediaType.parse(matcher.group(1))?.charset().let {
                if (it != null) return String(data, it)
            }
        }
        return dataDefaultEncoded
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readLastUpdated(html: String): String {
        val matcher = Pattern.compile("(Stand: ..\\...\\..... ..:..)")
                .matcher(html)

        if (matcher.find()) {
            return matcher.group(1)
        } else {
            throw ParseException("Could not find lastUpdated value in the document.")
        }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readDayAndDate(vplanDoc: Document): String {
        try {
            return vplanDoc.getElementsByClass("mon_title").first().allElements.first().text()
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan dateAndDay", e)
        }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readMotdTable(doc: Document): String {
        return doc.getElementsByClass("info")
                .select("tr")
                .map { it.select("td") }
                .filter { it.isNotEmpty() }
                .joinToString("<br>") {
                    val content = it.joinToString(" | ") {
                        it.html().split("<br>").joinToString("<br>") { it.trim() }
                    }
                    if (it.first().text().toLowerCase().contains("unterrichtsfrei")) {
                        "<font color=#FF5252>$content</font>"
                    } else {
                        content
                    }
                }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTable(doc: Document): List<VPlanRow> {
        val vPlanTable = doc.getElementsByClass("list").select("tr")
        try {
            return vPlanTable
                    .map { it.children() }
                    .filter { it.select("th").size == 0 }
                    .map { readVPlanTableCells(it) }
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan table", e)
        }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTableCells(cells: Elements): VPlanRow {
        fun read(element: Element): String {
            val span = element.getElementsByTag("span")
            val out = if (span.first() != null) span.first().html() else element.text()
            return out.replace("&nbsp;", " ").trim()
        }
        try {
            if (cells.size >= 6) {
                val valGrade = read(cells[GRADE_C])
                val valHour = read(cells[HOUR_C])
                val valContent = read(cells[CONTENT_C])
                val valSubject = read(cells[SUBJECT_C])
                val valRoom = read(cells[ROOM_C])
                val valKind = read(cells[KIND_C])

                val valOmitted = cells[KIND_C].text().toLowerCase().contains("entfall")
                val valMarkedNew = cells[GRADE_C].attr("style").matches("background-color: #00[Ff][Ff]00".toRegex())

                return VPlanRow(valSubject, valOmitted, valHour, valRoom, valContent,
                        valGrade, valKind, valMarkedNew)
            }
            throw ParseException("Could not parse row, invalid format.")
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan row", e)
        }
    }
}

