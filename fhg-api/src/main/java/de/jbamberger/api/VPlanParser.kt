package de.jbamberger.api

import de.jbamberger.api.data.VPlanDay
import de.jbamberger.api.data.VPlanRow
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Converter
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.IllegalCharsetNameException
import java.nio.charset.UnsupportedCharsetException
import java.util.*
import java.util.regex.Pattern

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
internal class VPlanParser : Converter<ResponseBody, VPlanDay> {

    @Throws(IOException::class)
    override fun convert(body: ResponseBody): VPlanDay {
        val data = body.bytes()
        var doc: Document? = null
        var html: String? = null
        val type = body.contentType()
        var charset: Charset? = type?.charset()
        if (charset != null) {
            Timber.i("Using header charset '%s'", charset.name())
            html = String(data, charset)
            doc = Jsoup.parse(html)
        } else {
            val tmpHtml = String(data)
            val tmpDoc = Jsoup.parse(tmpHtml)
            val head = tmpDoc.getElementsByTag("head").first()
            if (head != null) {
                val contentType = head.getElementsByAttributeValue("http-equiv", "Content-Type").first()
                if (contentType != null && contentType.hasAttr("content")) {
                    val contentAttr = contentType.attr("content")

                    try {
                        val matcher = Pattern.compile("\\s*([^;]+)(;\\s*)?")
                                .matcher(contentAttr)
                        val charsetPattern = Pattern.compile("^charset=([^\\s]*)$")

                        while (matcher.find()) {
                            val m = charsetPattern.matcher(matcher.group(1))
                            if (m.find()) {
                                charset = Charset.forName(m.group(1))
                                break
                            }
                        }
                        if (charset != null) {
                            Timber.i("Using html charset '%s'", charset.name())
                            html = String(data, charset)
                            doc = Jsoup.parse(html)
                        }

                    } catch (e: IllegalCharsetNameException) {
                        //no match or illegal charset
                        Timber.w(e, "charset extraction failed.")
                    } catch (e: UnsupportedCharsetException) {
                        Timber.w(e, "charset extraction failed.")
                    } catch (e: IllegalStateException) {
                        Timber.w(e, "charset extraction failed.")
                    }

                }
            }
            if (doc == null) {
                doc = tmpDoc
            }
            if (html == null) {
                html = tmpHtml
            }
        }


        val dateAndDay = readVPlanStatus(html)
        val lastUpdated = readVPlanTitle(doc!!)
        val motd = readMotdTable(doc.getElementsByClass("info"))
        val entries = readVPlanTable(doc.getElementsByClass("list").select("tr"))

        return VPlanDay(dateAndDay, lastUpdated, motd, entries)
    }

    companion object {

        private val GRADE_C = 0
        private val HOUR_C = 1
        private val SUBJECT_C = 2
        private val ROOM_C = 3
        private val KIND_C = 4
        private val CONTENT_C = 5


        @Throws(ParseException::class)
        private fun readVPlanStatus(html: String): String {
            try {
                var status = html.split("</head>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                status = status.split("<p>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].replace("\n", "").replace("\r", "")
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
                val rows = ArrayList<VPlanRow>()
                for (line in vPlanTable) {
                    val cells = line.children()
                    if (cells.select("th").size == 0) {
                        rows.add(readVPlanTableCells(cells))
                    }
                }
                return rows
            } catch (e: Exception) {
                throw ParseException("Could not parse vplan table", e)
            }

        }

        @Throws(ParseException::class)
        private fun readVPlanTableCells(cells: Elements): VPlanRow {
            try {
                if (cells.size >= 6) {
                    val grade = cells[GRADE_C].getElementsByTag("span")
                    val hour = cells[HOUR_C].getElementsByTag("span")
                    val subject = cells[SUBJECT_C].getElementsByTag("span")
                    val room = cells[ROOM_C].getElementsByTag("span")
                    val kind = cells[KIND_C].getElementsByTag("span")
                    val content = cells[CONTENT_C].getElementsByTag("span")

                    val valGrade = if (grade != null && grade.first() != null)
                        grade.first().html()
                    else
                        cells[GRADE_C].text()
                    val valHour = if (hour != null && hour.first() != null)
                        hour.first().html()
                    else
                        cells[HOUR_C].text()
                    val valContent = if (content != null && content.first() != null)
                        content.first().html()
                    else
                        cells[CONTENT_C].text()
                    val valSubject = if (subject != null && subject.first() != null)
                        subject.first().html()
                    else
                        cells[SUBJECT_C].text()
                    val valRoom = if (room != null && room.first() != null)
                        room.first().html()
                    else
                        cells[ROOM_C].text()
                    val valKind = if (kind != null && kind.first() != null)
                        kind.first().html()
                    else
                        cells[KIND_C].text()
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
}
