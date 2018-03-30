package de.jbamberger.fhg.repository

import de.jbamberger.fhg.repository.api.VPlanParser.readWithEncoding
import okhttp3.MediaType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.nio.charset.Charset

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class VPlanParserTest {

    companion object {

        fun load(name: String): ByteArray {
            val inStream = VPlanParserTest::class.java.classLoader
                    .getResourceAsStream(name)
            return inStream.readBytes(2048)
        }
    }

    @Test
    @Throws(Exception::class)
    fun test_parseContentTypeHeader() {
        val withEnc = load("v1.html")

        assertThat(readWithEncoding(withEnc, null),
                `is`(equalTo(String(withEnc, Charset.forName("windows-1252")))))

        assertThat(readWithEncoding(withEnc, MediaType.parse("text/html; charset=\"utf-8\"")),
                `is`(equalTo(String(withEnc, Charset.forName("utf-8")))))

        val withoutEncoding = load("v1-nohead.html")

        assertThat(readWithEncoding(withoutEncoding, null),
                `is`(equalTo(String(withoutEncoding, Charset.defaultCharset()))))
    }
}