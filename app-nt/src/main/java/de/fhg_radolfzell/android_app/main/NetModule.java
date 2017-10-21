package de.fhg_radolfzell.android_app.main;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.LocalDateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.BuildConfig;
import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;
import de.fhg_radolfzell.android_app.util.LocalDateTimeDeSerializer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
public class NetModule {

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeSerializer());
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (cache != null) {
            builder.cache(cache);
        }
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        return builder.build();


    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitAPI(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                //.addConverterFactory(SimpleXmlConverterFactory.create())//TODO produces errors, different handling necessary
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient);
    }

    @Provides
    @Singleton
    FhgApiInterface provideFhgApiInterface(Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder.baseUrl(FhgApiInterface.BASE_URL).build().create(FhgApiInterface.class);
    }

    @Provides
    @Singleton
    FhgWebInterface provideFhgWebInterface(Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder.baseUrl(FhgWebInterface.BASE_URL).build().create(FhgWebInterface.class);
    }
}
