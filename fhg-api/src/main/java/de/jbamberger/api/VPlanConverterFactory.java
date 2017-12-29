package de.jbamberger.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import de.jbamberger.api.data.VPlanDay;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanConverterFactory extends Converter.Factory {

    public static VPlanConverterFactory create() {
        return new VPlanConverterFactory();
    }

    private VPlanConverterFactory() {
    }

    @Nullable
    @Override
    public Converter<ResponseBody, VPlanDay> responseBodyConverter(
            @NonNull Type type, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (type.equals(VPlanDay.class)) {
            return new VPlanParser();
        } else {
            return null;
        }
    }
}
