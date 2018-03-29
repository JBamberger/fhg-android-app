package de.jbamberger.fhg.repository.api

import de.jbamberger.api.data.VPlanDay
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class VPlanConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, VPlanDay>? {
        return when (type) {
            VPlanDay::class.java -> VPlanConverter()
            else -> null
        }
    }
}
