package de.jbamberger.fhg.repository.api

import androidx.annotation.VisibleForTesting
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*
import java.util.regex.Pattern

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
internal object VPlanParser {

    private val REGEX_LAST_UPDATE = "Stand: ..\\...\\..... ..:..".toRegex()
    private val PATTERN_OMITTED = "entfall|x".toRegex()

    private const val COL_KIND = "kind"
    private const val COL_HOUR = "hour"
    private const val COL_CLASS = "class"
    private const val COL_ROOM = "room"
    private const val COL_SUBJECT = "subject"
    private const val COL_SUBST_TEXT = "text"
    private const val COL_NR = "nr"
    private const val COL_TEACHER = "teacher"
    private const val COL_SUBST_FROM = "subst-from"
    private const val COL_SUBST_TO = "subst-to"


    private val SCHEMA = mapOf(
            "vertretungsnummer|vtr-nr\\." to COL_NR,
            "art|entfall" to COL_KIND,
            "stunde" to COL_HOUR,
            "klasse(n|\\(n\\))?" to COL_CLASS,
            "\\(?lehrer\\)?" to COL_TEACHER,
            "\\(?raum\\)?" to COL_ROOM,
            "\\(?fach\\)?" to COL_SUBJECT,
            "vertr.\\s+von" to COL_SUBST_FROM,
            "\\(le\\.\\) nach" to COL_SUBST_TO,
            "(vertretungs)?-?text" to COL_SUBST_TEXT
    ).mapKeys { it.key.toRegex(RegexOption.IGNORE_CASE) }

    @Throws(ParseException::class)
    internal fun parseVPlanDay(body: ResponseBody): VPlanDay {
        val docString = readWithEncoding(body)
        val doc = Jsoup.parse(docString)
        return VPlanDay(VPlanHeader(readDayAndDate(doc), readLastUpdated(docString), readMotdTable(doc)),
                readVPlanTable(doc))
    }

    @VisibleForTesting
    internal fun readWithEncoding(body: ResponseBody): String {
        val data = body.bytes()

        body.contentType()?.charset()?.let {
            return String(data, it)
        }

        val dataDefaultEncoded = String(data)
        val matcher = Pattern
                .compile("<meta\\s+http-equiv=\"Content-Type\"\\s+content=\"([^\"]*)\"\\s*/?>")
                .matcher(dataDefaultEncoded)

        while (matcher.find()) {
            MediaType.parse(matcher.group(1)!!)?.charset().let {
                if (it != null) return String(data, it)
            }
        }
        return dataDefaultEncoded
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readLastUpdated(html: String) = REGEX_LAST_UPDATE.find(html)?.value
            ?: throw ParseException("Could not find lastUpdated value in the document.")

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readDayAndDate(vplanDoc: Document) = vplanDoc
            .getElementsByClass("mon_title")
            .singleOrNull()
            ?.allElements
            ?.firstOrNull()
            ?.text()
            ?: throw ParseException("Could not parse VPlan date and day.")

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readMotdTable(doc: Document) = doc
            .getElementsByClass("info")
            .select("tr")
            .map { it.select("td") }
            .filter(Elements::isNotEmpty)
            .joinToString(separator = "<br>") { cells ->
                cells.joinToString(separator = " | ") { cell ->
                    cell.html()
                            .split("<br>")
                            .joinToString(separator = "<br>", transform = String::trim)
                }
//                    if (it.first().text().toLowerCase(Locale.ROOT).contains("unterrichtsfrei")) {
//                        "<font color=#FF5252>$content</font>"
//                    } else {
//                        content
//                    }
            }


    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun createHeaderColumnMapping(header: Element): Map<String, Int> {
        if (header.tagName() != "tr") {
            throw ParseException("First VPlan row is not the header. tag=${header.tagName()}")
        }

        return header.children().mapIndexed { index, col ->
            if (col.tagName() != "th") {
                throw ParseException("Header contained column with wrong tagName=${col.tagName()}.")
            }

            val colTitle = col.html().trim().toLowerCase(Locale.ROOT)
            val colName = SCHEMA
                    .filter { it.key.matches(colTitle) }
                    .map { it.value }
                    .singleOrNull()
                    ?: throw ParseException("Header column could not be assigned uniquely. Title=$colTitle")
            return@mapIndexed colName to index
        }.toMap()
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTable(doc: Document): List<VPlanRow> {
        val rows = doc.getElementsByTag("table")
                .singleOrNull { it.hasClass("mon_list") }
                ?.getElementsByTag("tbody")
                ?.singleOrNull()
                ?.children()
                ?: throw ParseException("Couldn't locate VPlan table.")

        if (rows.isEmpty()) {
            throw ParseException("The VPlan table has no header")
        }

        val colMap = createHeaderColumnMapping(rows.removeAt(0))

        return rows.map { readVPlanTableCells(it.children(), colMap) }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTableCells(cells: Elements, colMap: Map<String, Int>): VPlanRow {
        fun read(element: Element) = (element.getElementsByTag("span")
                .singleOrNull()
                ?.html() ?: element.text())
                .replace("&nbsp;", " ")
                .trim()

        fun resolveIndex(colName: String) = colMap[colName]
                ?: throw ParseException("could not resolve column name $colName")

        fun resolve(colName: String) = read(cells[resolveIndex(colName)])

        if (cells.size >= 6) {
            val valGrade = resolve(COL_CLASS)
            val valHour = resolve(COL_HOUR)
            val valContent = resolve(COL_SUBST_TEXT)
            val valSubject = resolve(COL_SUBJECT)
            val valRoom = resolve(COL_ROOM)
            val valKind = resolve(COL_KIND).let {
                if (it.toLowerCase(Locale.ROOT) == "x") "entfall" else it
            }
//                val valSubNr = resolve(COL_SUB_NR)
//                val valSubTeacher = resolve(COL_SUBST_TEACHER)
//                val valSubFrom = resolve(COL_SUBST_FROM)
//                val valSubTo = resolve(COL_SUBST_TO)

            val valOmitted = cells[resolveIndex(COL_KIND)].text()
                    .toLowerCase(Locale.ROOT)
                    .matches(PATTERN_OMITTED)
            val valMarkedNew = cells[resolveIndex(COL_CLASS)]
                    .attr("style")
                    .matches("background-color: #00[Ff][Ff]00".toRegex())

            return VPlanRow(
                    valSubject, valOmitted, valHour, valRoom,
                    valContent, valGrade, valKind, valMarkedNew/*,
                        valSubNr, valSubTeacher, valSubFrom, valSubTo*/)
        }
        throw ParseException("Could not parse row, invalid format.")
    }
}

