package de.jbamberger.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import de.jbamberger.api.data.FeedChunk;
import de.jbamberger.api.data.FeedItem;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static de.jbamberger.util.Preconditions.checkNotNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class FeedConverterFactory extends Converter.Factory {

    @NonNull
    private final Gson gson;

    @Inject
    FeedConverterFactory(@NonNull Gson gson) {
        this.gson = checkNotNull(gson);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, FeedChunk> responseBodyConverter(
            @NonNull Type type, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (type.equals(FeedChunk.class)) {
            return body -> {
                Type listType = new TypeToken<List<FeedItem>>() {}.getType();
                List<FeedItem> items = gson.fromJson(body.string(), listType);
                return new FeedChunk(items);
            };
        } else {
            return null;
        }
    }
}