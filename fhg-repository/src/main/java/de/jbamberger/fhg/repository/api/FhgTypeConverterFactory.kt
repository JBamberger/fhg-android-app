package de.jbamberger.fhg.repository.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.jbamberger.api.data.FeedItem
import de.jbamberger.api.data.VPlanDay
import de.jbamberger.fhg.repository.api.FhgTypeConverterFactory.FeedConverter.Companion.FEED_TYPE
import de.jbamberger.fhg.repository.api.FhgTypeConverterFactory.VPlanConverter.Companion.VPLAN_TYPE
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class FhgTypeConverterFactory
private constructor(private val moshi: Moshi) : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, *>? {
        Timber.d("get converter for type: $type")

        return when (type) {
            VPLAN_TYPE -> VPlanConverter()
            FEED_TYPE -> FeedConverter(moshi)
            else -> null
        }
    }

    private class VPlanConverter : Converter<ResponseBody, VPlanDay> {
        @Throws(IOException::class)
        override fun convert(body: ResponseBody): VPlanDay {
            try {
                return VPlanParser.parseVPlanDay(body)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e;
            }
        }

        companion object {
            val VPLAN_TYPE: Type = VPlanDay::class.java
        }
    }

    private class FeedConverter(moshi: Moshi) : Converter<ResponseBody, List<FeedItem>> {
        private val adapter = moshi.adapter<List<FeedItem>>(FEED_TYPE)

        override fun convert(value: ResponseBody?): List<FeedItem> {
            return if (value != null) {
                adapter.fromJson(value.source()) ?: emptyList()
            } else {
                emptyList()
            }
        }

        companion object {
            val FEED_TYPE: Type = Types.newParameterizedType(List::class.java, FeedItem::class.java)
        }
    }

    companion object {
        fun create(moshi: Moshi) : FhgTypeConverterFactory {
            return FhgTypeConverterFactory(moshi)
        }
    }
}