package de.jbamberger.fhg.repository

import de.jbamberger.fhg.repository.api.VPlanParser.parseVPlanDay
import de.jbamberger.fhg.repository.api.VPlanParser.readDayAndDate
import de.jbamberger.fhg.repository.api.VPlanParser.readLastUpdated
import de.jbamberger.fhg.repository.api.VPlanParser.readMotdTable
import de.jbamberger.fhg.repository.api.VPlanParser.readVPlanTable
import de.jbamberger.fhg.repository.api.VPlanParser.readWithEncoding
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.jsoup.Jsoup
import org.junit.Assert.assertThat
import org.junit.Test
import java.nio.charset.Charset

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanParserTest {

    companion object {
        private val DEFAULT_CHARSET: Charset = Charset.forName("windows-1252")
        private const val v1_motd =
                "<b>SMV-Treffen</b> am Montag, 9.4.18 in der 6. Std. im Olymp!<br>" +
                        "<b>K1 Französisch: Klausurbeginn um 09:20 Uhr!</b><br>" +
                        "<br>" +
                        "K1 Studienfahrt Hamburg trifft sich in der zweiten großen Pause in Raum 127!"
        private const val v1_lastUpdated = "Stand: 23.03.2018 10:22"
        private const val v1_dayAndDate = "23.3.2018 Freitag"

        private const val v2_motd = "K1/2 haben unterrichtsfrei.<br>" +
                "Die Schüler aus der 10b nehmen diese Woche am Unterricht der 10a Teil.<br>" +
                "<b>SMV-Treffen</b> heute (Mo) in der 6. Std. im Olymp!"

        private fun getV1Table(): List<VPlanRow> {
            return listOf(
                    VPlanRow("Gmk_1", false, "3 - 4", "104", "[WolflenzA]: Raumänderung", "K2", "Raum-Vtr.", false),
                    VPlanRow("<s>gk_1</s>", true, "5 - 6", "---", "", "K2", "Entfall", false),
                    VPlanRow("<s>M-Diff</s>", true, "4", "<s>224</s>", "", "<s>10c</s>", "Entfall", false),
                    VPlanRow("<s>L</s>", true, "5", "<s>226</s>", "", "<s>10c, 10a, 10b</s>", "Entfall", false),
                    VPlanRow("<s>F</s>", true, "5", "<s>224</s>", "", "<s>10c</s>", "Entfall", false),
                    VPlanRow("M", false, "1 - 2", "207", "Mathe", "9a", "Vertretung", false),
                    VPlanRow("F", false, "1", "119", "F bei Fr. E. findet statt", "7a, 7b, 7c", "Unterricht geändert", false),
                    VPlanRow("L", false, "1 - 2", "<s>118</s>?105", "", "7a, 7b, 7c", "Raum-Vtr.", true),
                    VPlanRow("Bio", false, "5", "<s>023, 028</s>?067", "", "5d", "Vertretung", true)
            )
        }

        private fun load(name: String): ByteArray {
            val inStream = VPlanParserTest::class.java.classLoader!!.getResourceAsStream(name)
            return inStream.readBytes()
        }

        private fun loadAsString(name: String) = String(load(name), DEFAULT_CHARSET)
    }

    @Test
    @Throws(Exception::class)
    fun test_parseVPlanDay() {
        val day = VPlanDay(VPlanHeader(v1_dayAndDate, v1_lastUpdated, v1_motd), getV1Table())

        assertThat(parseVPlanDay(ResponseBody.create(MediaType.parse("text/html; charset=\"windows-1252\""), load("v1.html"))), `is`(equalTo(day)))
    }

    @Test
    @Throws(Exception::class)
    fun test_readVPlanTable() {
        val plan = loadAsString("v1.html")
        assertThat(readVPlanTable(Jsoup.parse(plan)), `is`(equalTo(getV1Table())))
    }

    @Test
    @Throws(Exception::class)
    fun test_readDayAndDate() {
        val plan = loadAsString("v1.html")
        assertThat(readDayAndDate(Jsoup.parse(plan)), `is`(equalTo(v1_dayAndDate)))
    }

    @Test
    @Throws(Exception::class)
    fun test_readLastUpdated() {
        val plan = loadAsString("v1.html")

        assertThat(readLastUpdated(plan), `is`(equalTo(v1_lastUpdated)))
    }

    @Test
    @Throws(Exception::class)
    fun test_readMotdTable() {
        val v1 = loadAsString("v1.html")
        assertThat(readMotdTable(Jsoup.parse(v1)), `is`(equalTo(v1_motd)))
        val v2 = loadAsString("v2.html")
        assertThat(readMotdTable(Jsoup.parse(v2)), `is`(equalTo(v2_motd)))
    }

    @Test
    @Throws(Exception::class)
    fun test_readWithEncoding() {
        val withEnc = load("v1.html")
        assertThat(readWithEncoding(ResponseBody.create(null, withEnc)),
                `is`(equalTo(String(withEnc, Charset.forName("windows-1252")))))

        assertThat(readWithEncoding(ResponseBody.create(MediaType.parse("text/html; charset=\"utf-8\""), withEnc)),
                `is`(equalTo(String(withEnc, Charset.forName("utf-8")))))

        val withoutEncoding = load("v1-nohead.html")

        assertThat(readWithEncoding(ResponseBody.create(null, withoutEncoding)),
                `is`(equalTo(String(withoutEncoding, Charset.defaultCharset()))))
    }
}