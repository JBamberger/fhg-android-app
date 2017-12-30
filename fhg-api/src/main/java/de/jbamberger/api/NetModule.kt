package de.jbamberger.api

import android.content.Context

import com.google.gson.Gson

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class NetModule private constructor() {

    init {
        throw AssertionError("No instances.")
    }

    companion object {

        fun getEndpoint(context: Context): FhgEndpoint {
            return provideFhgEndpoint(provideRetrofitAPI(provideOkHttpClient(provideOkHttpCache(context))))
        }

        private fun provideOkHttpCache(context: Context): Cache {
            val cacheSize = 10 * 1024 * 1024 // 10 MiB
            return Cache(context.cacheDir, cacheSize.toLong())
        }

        private fun provideOkHttpClient(cache: Cache): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.cache(cache)

            /*if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }*/

            return builder.build()
        }

        private fun provideRetrofitAPI(okHttpClient: OkHttpClient): Retrofit.Builder {
            val gson = Gson()
            return Retrofit.Builder()
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .addConverterFactory(VPlanConverterFactory())
                    .addConverterFactory(FeedConverterFactory(gson))
                    //.addConverterFactory(SimpleXmlConverterFactory.create())
                    //                .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
        }

        private fun provideFhgEndpoint(retrofitBuilder: Retrofit.Builder): FhgEndpoint {
            return retrofitBuilder.baseUrl(FhgEndpoint.BASE_URL).build().create(FhgEndpoint::class.java)
        }
    }
}
