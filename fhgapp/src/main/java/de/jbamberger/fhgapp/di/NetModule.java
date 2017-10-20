package de.jbamberger.fhgapp.di;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.jbamberger.fhgapp.BuildConfig;
import de.jbamberger.fhgapp.source.FhgApi;
import de.jbamberger.fhgapp.util.LiveDataCallAdapterFactory;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
class NetModule {

    @Provides
    @Singleton
    Cache provideOkHttpCache(@NonNull Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeSerializer());
//        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
//        gsonBuilder.registerTypeAdapter(byte[].class, new ByteArrayTypeAdapter());
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(@NonNull Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitAPI(@NonNull Gson gson, @NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                //.addConverterFactory(SimpleXmlConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient);
    }

    @Provides
    @Singleton
    FhgApi provideFhgApi(@NonNull Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder.baseUrl(FhgApi.BASE_URL).build().create(FhgApi.class);
    }
}
