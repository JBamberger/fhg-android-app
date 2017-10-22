package de.jbamberger.api;

import android.content.Context;
import android.support.annotation.NonNull;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

final class NetModule {

    private NetModule() {throw new AssertionError("No instances.");}

    static FhgEndpoint getEndpoint(@NonNull Context context) {
        return provideFhgEndpoint(provideRetrofitAPI(provideOkHttpClient(provideOkHttpCache(context))));
    }

    private static Cache provideOkHttpCache(@NonNull Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    private static OkHttpClient provideOkHttpClient(@NonNull Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        return builder.build();
    }

    private static Retrofit.Builder provideRetrofitAPI(@NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(VPlanConverterFactory.create())
                //.addConverterFactory(SimpleXmlConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient);
    }

    private static FhgEndpoint provideFhgEndpoint(@NonNull Retrofit.Builder retrofitBuilder) {
        return retrofitBuilder.baseUrl(FhgEndpoint.BASE_URL).build().create(FhgEndpoint.class);
    }
}
