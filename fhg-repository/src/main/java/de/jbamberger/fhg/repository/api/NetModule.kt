package de.jbamberger.fhg.repository.api

import android.content.Context
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import de.jbamberger.fhg.repository.BuildConfig
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
internal class NetModule {

    companion object {
        private const val CACHE_SIZE = 10 * 1024 * 1024.toLong() //10 MiB
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache {
        return Cache(context.cacheDir, CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.cache(cache)

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Timber.d(it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(logger)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitAPI(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(FhgTypeConverterFactory.create(moshi))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideFhgEndpoint(retrofitBuilder: Retrofit.Builder): FhgEndpoint {
        return retrofitBuilder.baseUrl(HttpUrl.parse(FhgEndpoint.BASE_URL)!!)
                .build()
                .create(FhgEndpoint::class.java)
    }
}
