package de.jbamberger.fhg.repository.api

import androidx.annotation.VisibleForTesting
import de.jbamberger.fhg.repository.api.VPlanParser.Columns.*
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

    internal enum class Columns(pattern: String) {
        NR("vertretungsnummer|vtr-nr\\."),
        KIND("art|entfall"),
        HOUR("stunde"),
        CLASS("klasse(n|\\(n\\))?"),
        TEACHER("\\(?lehrer\\)?"),
        ROOM("\\(?raum\\)?"),
        SUBJECT("\\(?fach\\)?"),
        SUBST_FROM("vertr.\\s+von"),
        SUBST_TO("\\(le\\.\\) nach"),
        CONTENT("(vertretungs)?-?text");

        val regex = pattern.toRegex(RegexOption.IGNORE_CASE)
    }

    @Throws(ParseException::class)
    internal fun parseVPlanDay(body: ResponseBody): VPlanDay {
        val docString = readWithEncoding(body)
        val doc = Jsoup.parse(docString)
        return VPlanDay(
            VPlanHeader(readDayAndDate(doc), readLastUpdated(docString), readMotdTable(doc)),
            readVPlanTable(doc)
        )
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
            matcher.group(1)!!.toMediaTypeOrNull()?.charset().let {
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
    internal fun createHeaderColumnMapping(header: Element): Map<Columns, Int> {
        if (header.tagName() != "tr") {
            throw ParseException("First VPlan row is not the header. tag=${header.tagName()}")
        }

        return header.children().mapIndexed { index, col ->
            if (col.tagName() != "th") {
                throw ParseException("Header contained column with wrong tagName=${col.tagName()}.")
            }

            val colTitle = col.html().trim().lowercase(Locale.ROOT)
            val colName = Columns.values()
                .singleOrNull { it.regex.matches(colTitle) }
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

        if (rows.size == 1) {
            val firstRow = rows.removeAt(0)
            firstRow.text().contains("keine\\s+vertretungen".toRegex(RegexOption.IGNORE_CASE))
            return emptyList()
        }

        val colMap = createHeaderColumnMapping(rows.removeAt(0))

        return rows.map { readVPlanTableCells(it.children(), colMap) }
    }

    @VisibleForTesting
    @Throws(ParseException::class)
    internal fun readVPlanTableCells(cells: Elements, colMap: Map<Columns, Int>): VPlanRow {
        fun read(element: Element) = (element.getElementsByTag("span")
            .singleOrNull()
            ?.html() ?: element.text())
            .replace("&nbsp;", " ")
            .trim()

        fun resolveIndex(colName: Columns) = colMap[colName]
            ?: throw ParseException("could not resolve column name $colName")

        fun resolve(colName: Columns) = read(cells[resolveIndex(colName)])

        fun resolveOptional(colName: Columns) = try {
            resolve(colName)
        } catch (e: ParseException) {
            null
        }

        if (cells.size >= 6) {

            val isOmitted = cells[resolveIndex(KIND)].text()
                .lowercase(Locale.ROOT)
                .matches(PATTERN_OMITTED)
            val isNew = cells[resolveIndex(CLASS)]
                .attr("style")
                .matches("background-color: #00[Ff][Ff]00".toRegex())

            return VPlanRow(
                resolve(SUBJECT),
                isOmitted,
                resolve(HOUR),
                resolve(ROOM),
                resolve(CONTENT),
                resolve(CLASS),
                resolve(KIND).let { if (it.lowercase(Locale.ROOT) == "x") "entfall" else it },
                isNew,
                resolveOptional(NR),
                resolveOptional(TEACHER),
                resolveOptional(SUBST_FROM),
                resolveOptional(SUBST_TO)
            )
        }
        throw ParseException("Could not parse row, invalid format.")
    }
}

