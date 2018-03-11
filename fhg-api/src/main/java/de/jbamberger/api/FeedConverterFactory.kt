package de.jbamberger.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.jbamberger.api.data.FeedItem
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FeedConverterFactory @Inject
internal constructor(private val moshi: Moshi) : Converter.Factory() {

    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit):
            Converter<ResponseBody, List<FeedItem>>? {

        Timber.d("get converter for type: $type")

        val listType = Types.newParameterizedType(List::class.java, FeedItem::class.java)

        return when (type) {
            listType -> {
                object : Converter<ResponseBody, List<FeedItem>> {
                    private val adapter = moshi.adapter<List<FeedItem>>(type)

                    override fun convert(value: ResponseBody?): List<FeedItem> {
                        return if (value != null) {
                            adapter.fromJson(value.source()) ?: emptyList()
                        } else {
                            emptyList()
                        }
                    }
                }
            }
            else -> null
        }
    }
}