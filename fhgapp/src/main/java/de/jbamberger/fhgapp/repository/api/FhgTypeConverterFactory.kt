package de.jbamberger.fhgapp.repository.api

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.jbamberger.fhgapp.repository.api.FhgTypeConverterFactory.FeedConverter.Companion.FEED_TYPE
import de.jbamberger.fhgapp.repository.api.FhgTypeConverterFactory.UntisVPlanConverter.Companion.UNTIS_VPLAN_TYPE
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.VPlanDay
import de.jbamberger.fhgapp.repository.data.VPlanHeader
import de.jbamberger.fhgapp.repository.data.VPlanRow
import de.jbamberger.fhgapp.repository.util.unescapeHtml
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class FhgTypeConverterFactory
internal constructor(private val moshi: Moshi) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return when (type) {
            UNTIS_VPLAN_TYPE -> UntisVPlanConverter(moshi)
            FEED_TYPE -> FeedConverter(moshi)
            else -> null
        }
    }

    private class UntisVPlanConverter(moshi: Moshi) : Converter<ResponseBody, VPlanDay> {
        private val adapter = moshi.adapter<UntisResponse<UntisVPlanDay>>(
            Types.newParameterizedType(UntisResponse::class.java, UntisVPlanDay::class.java)
        )
        private val originalFormat = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
        private val newFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)

        override fun convert(value: ResponseBody?): VPlanDay {
            val response: UntisResponse<UntisVPlanDay> = value?.source()?.use {
                try {
                    adapter.fromJson(it)
                } catch (e: JsonDataException) {
                    Timber.e(e, "Failed to parse json")
                    throw e
                }
            } ?: throw IOException("Received invalid empty response or failed to parse result!")

            val err = response.error
            if (err != null) {
                throw IOException("Received error response with code ${err.code} and message ${err.message}")
            }

            val payload =
                response.payload ?: throw IOException("Received message with empty payload.")

            val motdBuilder = StringBuilder()
            payload.messageData?.messages?.forEach {
                var isNotEmpty = false
                if (!it.subject.isNullOrBlank()) {
                    motdBuilder.append(it.subject)
                    isNotEmpty = true
                }
                if (!it.body.isNullOrBlank()) {
                    if (isNotEmpty) {
                        motdBuilder.append(": ")
                    }
                    motdBuilder.append(it.body)
                    isNotEmpty = true
                }
                if (isNotEmpty) {
                    motdBuilder.append("\n<br>\n")
                }
            }

            val lastUpdated = payload.lastUpdate ?: ""

            val dateAndDay = StringBuilder()
            payload.weekDay?.let {
                dateAndDay.append(it).append(", ")
            }
            try {
                val date = originalFormat.parse(payload.date.toString())
                dateAndDay.append(newFormat.format(date!!))
            } catch (e: java.text.ParseException) {
                dateAndDay.append(payload.date.toString())
            }

            val header = VPlanHeader(dateAndDay.toString(), lastUpdated, motdBuilder.toString())

            val rows = payload.rows.map { row ->
                val cleanInfo = row.info.unescapeHtml().trim().lowercase()
                val kind = when {
                    cleanInfo.startsWith("entfall") || cleanInfo == "x" -> "Entfall"
                    cleanInfo.startsWith("raumänderung") -> "Raumänderung"
                    cleanInfo.startsWith("verlegung") -> "Verlegung"
                    else -> ""
                }
                val isOmitted = kind == "Entfall"
                val content = when (kind) {
                    "Entfall", "Raumänderung" -> ""
                    else -> row.info + " "
                } + row.substText
                val isMarkedNew = false

                VPlanRow(
                    row.subject,
                    isOmitted,
                    row.hour,
                    row.room,
                    content,
                    row.grade,
                    kind,
                    isMarkedNew
                )
            }

            return VPlanDay(header, rows)
        }

        companion object {
            val UNTIS_VPLAN_TYPE: Type = VPlanDay::class.java
        }
    }

    private class FeedConverter(moshi: Moshi) : Converter<ResponseBody, List<FeedItem>> {
        private val adapter = moshi.adapter<List<FeedItem>>(FEED_TYPE)

        override fun convert(value: ResponseBody?): List<FeedItem> {
            return value?.source()?.use { adapter.fromJson(it) } ?: emptyList()
        }

        companion object {
            val FEED_TYPE: Type = Types.newParameterizedType(List::class.java, FeedItem::class.java)
        }
    }
}