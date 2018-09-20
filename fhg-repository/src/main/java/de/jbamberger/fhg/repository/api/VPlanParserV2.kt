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
internal object VPlanParserV2 {

    private const val COL_SUB_NR = "vtr-nr."
    private const val COL_KIND = "entfall" // "art"
    private const val COL_HOUR = "stunde"
    private const val COL_CLASS = "klasse(n)"
    private const val COL_SUBST_TEACHER = "vertreter"
    private const val COL_ROOM = "raum"
    private const val COL_SUBJECT = "fach"
    private const val COL_SUBST_FROM = "vertr. von"
    private const val COL_SUBST_TO = "(le.) nach"
    private const val COL_SUBST_TEXT = "vertretungs-text"


    private val SCHEMA = setOf(
            /*COL_SUB_NR, */COL_KIND, COL_HOUR, COL_CLASS, /*COL_SUBST_TEACHER,*/
            COL_ROOM, COL_SUBJECT/*, COL_SUBST_FROM, COL_SUBST_TO*/, COL_SUBST_TEXT)
    private val SCHEMA_SIZE = SCHEMA.size

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
    internal fun createHeaderColumnMapping(header: Element): Map<String, Int> {
        if (header.tagName() != "tr") {
            throw ParseException("First vplan row is not the header. tag=${header.tagName()}")
        }

        val headCols = header.children()
        if (headCols.size != SCHEMA_SIZE) {
            throw ParseException("Header.size=${headCols.size} differs from SCHEMA_SIZE=$SCHEMA_SIZE")
        }

        val colMap = headCols.mapIndexed { index, col ->
            if (col.tagName() != "th") {
                throw ParseException("Header contained column with wrong tagName=${col.tagName()}.")
            }

            val colTitle = col.html().trim().toLowerCase()
            if (SCHEMA.contains(colTitle)) {
                return@mapIndexed colTitle to index
            } else {
                throw ParseException("Header contained column with unknown title=$colTitle")
            }
        }.toMap()


        if (colMap.size != SCHEMA_SIZE) {
            throw ParseException("ColMap has different size than schema.")
        }
        return colMap
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTable(doc: Document): List<VPlanRow> {
        val tables = doc.getElementsByTag("table")
                .filter { it.hasClass("mon_list") }
        if (tables.size != 1) throw ParseException("There is more than one table with correct class")

        val rows = tables.first().getElementsByTag("tbody").first().children()
        if (rows == null || rows.size < 1) {
            throw ParseException("The VPlan table has no header")
        }

        val colMap = createHeaderColumnMapping(rows.removeAt(0))

        return rows.map { readVPlanTableCells(it.children(), colMap) }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTableCells(cells: Elements, colMap: Map<String, Int>): VPlanRow {
        fun read(element: Element): String {
            val span = element.getElementsByTag("span")
            val out = if (span.first() != null) span.first().html() else element.text()
            return out.replace("&nbsp;", " ").trim()
        }

        if (cells.size != SCHEMA_SIZE) {
            throw ParseException("RowSize=${cells.size} differs from SCHEMA_SIZE=$SCHEMA_SIZE")
        }

        val resolveIndex: (String) -> Int = { colName -> colMap[colName] ?: throw ParseException() }
        val resolve: (String) -> String = { colName -> read(cells[resolveIndex(colName)]) }

        try {
            if (cells.size >= 6) {
                val valGrade = resolve(COL_CLASS)
                val valHour = resolve(COL_HOUR)
                val valContent = resolve(COL_SUBST_TEXT)
                val valSubject = resolve(COL_SUBJECT)
                val valRoom = resolve(COL_ROOM)
                val valKind = resolve(COL_KIND).let {
                    if (it.toLowerCase() == "x") "entfall" else it
                }
//                val valSubNr = resolve(COL_SUB_NR)
//                val valSubTeacher = resolve(COL_SUBST_TEACHER)
//                val valSubFrom = resolve(COL_SUBST_FROM)
//                val valSubTo = resolve(COL_SUBST_TO)

                val valOmitted = cells[resolveIndex(COL_KIND)].text()
                        .toLowerCase()
                        .matches("entfall|x".toRegex())
                val valMarkedNew = cells[resolveIndex(COL_CLASS)]
                        .attr("style")
                        .matches("background-color: #00[Ff][Ff]00".toRegex())

                return VPlanRow(
                        valSubject, valOmitted, valHour, valRoom,
                        valContent, valGrade, valKind, valMarkedNew/*,
                        valSubNr, valSubTeacher, valSubFrom, valSubTo*/)
            }
            throw ParseException("Could not parse row, invalid format.")
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan row", e)
        }
    }
}

