package de.jbamberger.fhg_parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class FhgParserFactory extends Converter.Factory {

    public static FhgParserFactory create() {
        return new FhgParserFactory();
    }

    private FhgParserFactory() {
    }

    @Nullable
    @Override
    public Converter<ResponseBody, VPlan> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type.equals(VPlan.class)) {
            return new Converter<ResponseBody, VPlan>() {
                @Override
                public VPlan convert(@NonNull ResponseBody value) throws IOException {
                    return VPlanParser.parse(value.string());
                }
            };
        } else {
            return null;
        }
    }
}
