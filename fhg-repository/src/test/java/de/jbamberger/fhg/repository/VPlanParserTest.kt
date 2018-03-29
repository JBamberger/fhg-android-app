package de.jbamberger.fhg.repository

import de.jbamberger.fhg.repository.api.VPlanParser.DEFAULT_CHARSET
import de.jbamberger.fhg.repository.api.VPlanParser.parseContentTypeHeader
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.junit.Assert.assertThat
import org.junit.Test
import java.nio.charset.Charset

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanParserTest {

    companion object {
        private const val STREAM_END = "\\A"

        fun load(name: String): String {
            java.util.Scanner(VPlanParserTest::class.java.classLoader
                    .getResourceAsStream(name))
                    .use({ s ->
                        return if (s.useDelimiter(STREAM_END).hasNext()) s.next() else ""
                    })
        }
    }

    @Test
    @Throws(Exception::class)
    fun test_parseContentTypeHeader() {
        val csWin1252 = Charset.forName("windows-1252")

        val noCharset =
                "<meta http-equiv=\"Content-Type\" content=\"text/html\">"
        val invalidCharset =
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=nope\">"
        val noContentAttr =
                "<meta http-equiv=\"Content-Type\">"
        val wrongHttpAttr =
                "<meta http-equiv=\"Not-Content-Type\" content=\"text/html; charset=windows-1252\">"
        val wrongTagName =
                "<beta http-equiv=\"Content-Type\" content=\"charset=windows-1252\">"
        val noContentType =
                "<meta http-equiv=\"Content-Type\" content=\"charset=windows-1252\">"
        val multipleCharsets =
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252; charset=utf-8\">"

        val correctType =
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">"


        fun ct(s: String): Element? {
            return Jsoup.parse(s, "", Parser.xmlParser()).children()?.first()
        }

        // invalid inputs
        assertThat(parseContentTypeHeader(null), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct("")), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(noCharset)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(invalidCharset)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(noContentAttr)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(wrongHttpAttr)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(wrongTagName)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(noContentType)), `is`(equalTo(DEFAULT_CHARSET)))
        assertThat(parseContentTypeHeader(ct(multipleCharsets)), `is`(equalTo(DEFAULT_CHARSET)))

        //valid inputs
        assertThat(parseContentTypeHeader(ct(correctType)), `is`(equalTo(csWin1252)))
    }
}