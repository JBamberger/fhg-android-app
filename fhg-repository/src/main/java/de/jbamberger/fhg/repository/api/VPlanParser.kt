package de.jbamberger.fhg.repository.api

import android.support.annotation.VisibleForTesting
import de.jbamberger.api.data.VPlanDay
import de.jbamberger.api.data.VPlanHeader
import de.jbamberger.api.data.VPlanRow
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import java.nio.charset.Charset

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

    private const val PATTERN_GROUP = "\\s*([^;]+)(;\\s*)?"
    private const val PATTERN_CHARSET = "^charset\\s*=\\s*([^\\s]*)\\s*\$"
    val DEFAULT_CHARSET: Charset = Charset.forName("utf-8")

    @VisibleForTesting
    internal fun parseContentTypeHeader(contentType: Element?): Charset? {
        if (contentType == null) return DEFAULT_CHARSET
        if (contentType.tagName() != "meta") return DEFAULT_CHARSET
        if (contentType.attr("http-equiv") != "Content-Type") return DEFAULT_CHARSET

        val contentAttr = contentType.attr("content") ?: return DEFAULT_CHARSET

        val t = MediaType.parse(contentAttr)

        return t?.charset() ?: DEFAULT_CHARSET
    }

    @Throws(ParseException::class)
    internal fun parseVPlanDay(body: ResponseBody): VPlanDay {
        val data = body.bytes()
        val type = body.contentType()
        val charset: Charset? = type?.charset()
        if (charset != null) {
            Timber.i("Using header charset '%s'", charset.name())
            return parseVPlanDay(String(data, charset))
        }
        val tmpHtml = String(data)
        val tmpDoc = Jsoup.parse(tmpHtml)
        val head = tmpDoc.getElementsByTag("head").first() ?: return parseVPlanDay(tmpHtml)

        val contentType = head.getElementsByAttributeValue("http-equiv", "Content-Type").first()
        val c = parseContentTypeHeader(contentType)
        return parseVPlanDay(when (c) {
            null -> tmpHtml
            else -> String(data, c)
        })
    }

    @Throws(ParseException::class)
    private fun parseVPlanDay(html: String): VPlanDay {
        val doc = Jsoup.parse(html)
        return VPlanDay(VPlanHeader(readVPlanStatus(html), readVPlanTitle(doc), readMotdTable(doc)),
                readVPlanTable(doc))
    }

    @Throws(ParseException::class)
    private fun readVPlanStatus(html: String): String {
        try {
            var status = html.split("</head>".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
            status = status.split("<p>".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0]
                    .replace("\n", "")
                    .replace("\r", "")
            return status
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw ParseException("Could not parse vplan lastUpdated", e)
        }
    }

    @Throws(ParseException::class)
    private fun readVPlanTitle(vplanDoc: Document): String {
        try {
            return vplanDoc.getElementsByClass("mon_title").first().allElements.first().text()
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan dateAndDay", e)
        }
    }

    @Throws(ParseException::class)
    private fun readMotdTable(doc: Document): String {
        try {
            val tableRows = doc.getElementsByClass("info").select("tr")
            val motd = StringBuilder()
            for (line in tableRows) {
                val cells = line.children().select("td")
                val size = cells.size
                if (size == 0) {
                    continue
                }
                val highlightRow = size > 1 && cells.first()
                        .text().toLowerCase().contains("unterrichtsfrei")
                if (highlightRow) {
                    motd.append("<font color=#FF5252>")
                }
                for (i in 0 until size) {
                    val cell = cells[i]
                    motd.append(cell.toString())
                    if (i < size - 2) {
                        motd.append(" | ")
                    }
                }
                if (highlightRow) {
                    motd.append("</font>")
                }
                motd.append("<br />")
            }
            val dat = Jsoup.parse(motd.toString())
            dat.select("html").unwrap()
            dat.select("head").unwrap()
            dat.select("body").unwrap()
            dat.select("td").unwrap()
            return dat.toString()
        } catch (e: Exception) {
            throw ParseException("Could not parse motd table", e)
        }
    }

    @Throws(ParseException::class)
    private fun readVPlanTable(doc: Document): List<VPlanRow> {
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

    @Throws(ParseException::class)
    private fun readVPlanTableCells(cells: Elements): VPlanRow {
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

