package de.jbamberger.fhg.repository.api

import de.jbamberger.api.data.VPlanDay
import de.jbamberger.api.data.VPlanHeader
import de.jbamberger.api.data.VPlanRow
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.UnsupportedCharsetException
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

    private const val PATTERN_GROUP = "\\s*([^;]+)(;\\s*)?"
    private const val PATTERN_CHARSET = "^charset=([^\\s]*)\$"


    private fun parseContentTypeHeader(contentType: Element?): Charset? {
        val contentAttr = contentType?.attr("content") ?: return null

        try {
            val groupMatcher = Pattern.compile(PATTERN_GROUP).matcher(contentAttr)
            val charsetPattern = Pattern.compile(PATTERN_CHARSET)

            while (groupMatcher.find()) {
                val charsetMatcher = charsetPattern.matcher(groupMatcher.group(1))
                if (charsetMatcher.find()) return Charset.forName(charsetMatcher.group(1))
            }
        } catch (e: IllegalCharsetNameException) {
            //no match or illegal charset
            Timber.w(e, "charset extraction failed.")
        } catch (e: UnsupportedCharsetException) {
            Timber.w(e, "charset extraction failed.")
        } catch (e: IllegalStateException) {
            Timber.w(e, "charset extraction failed.")
        }
        return null
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
        val head = tmpDoc.getElementsByTag("head")
                .first() ?: return parseVPlanDay(tmpHtml)

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

        val header = VPlanHeader(
                readVPlanStatus(html),
                readVPlanTitle(doc),
                readMotdTable(doc.getElementsByClass("info")))
        val entries = readVPlanTable(doc.getElementsByClass("list").select("tr"))

        return VPlanDay(header, entries)
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
            return vplanDoc.getElementsByClass("mon_title")[0].allElements[0].text()
        } catch (e: Exception) {
            throw ParseException("Could not parse vplan dateAndDay", e)
        }
    }

    @Throws(ParseException::class)
    private fun readMotdTable(classInfo: Elements): String {
        try {
            val tableRows = classInfo.select("tr")
            val motd = StringBuilder()
            for (line in tableRows) {
                val cells = line.children().select("td")
                val size = cells.size
                if (size == 0) {
                    continue
                }
                val highlightRow = size > 1 && cells.first().text().toLowerCase().contains("unterrichtsfrei")
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
            var data = motd.toString()
            val dat = Jsoup.parse(data)
            dat.select("html").unwrap()
            dat.select("head").unwrap()
            dat.select("body").unwrap()
            dat.select("td").unwrap()
            data = dat.toString()
            return data
        } catch (e: Exception) {
            throw ParseException("Could not parse motd table", e)
        }
    }

    @Throws(ParseException::class)
    private fun readVPlanTable(vPlanTable: Elements): List<VPlanRow> {
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

