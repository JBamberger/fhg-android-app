package de.jbamberger.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.jbamberger.api.data.FeedItem
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FeedConverterFactory @Inject
internal constructor(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
            type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, List<FeedItem>>? {
        val listType = object : TypeToken<List<FeedItem>>() {}.type
        if (type == listType) {
            return Converter { body ->
                gson.fromJson<List<FeedItem>>(body.string(), listType).orEmpty()
            }
        } else {
            return null
        }
    }
}