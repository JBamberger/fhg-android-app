package de.jbamberger.fhgapp.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.jbamberger.fhgapp.repository.api.FhgEndpoint
import de.jbamberger.fhgapp.repository.api.FhgTypeConverterFactory
import de.jbamberger.fhgapp.repository.api.LiveDataCallAdapter
import de.jbamberger.fhgapp.repository.api.UntisFhgEndpoint
import de.jbamberger.fhgapp.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoBindingModule {

    @Binds
    @Singleton
    internal abstract fun bindRepository(impl: RepositoryImpl): Repository
}

@InstallIn(SingletonComponent::class)
@Module
internal class FhgRepositoryModule {

    companion object {
        private const val CACHE_SIZE = 10 * 1024 * 1024.toLong() //10 MiB
    }

    @Provides
    @Singleton
    internal fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, CACHE_SIZE)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.cache(cache)

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor {
                Timber.d(it)
            }
            logger.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(logger)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofitAPI(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(LiveDataCallAdapter.Factory())
            .addConverterFactory(FhgTypeConverterFactory(moshi))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
    }

    @Provides
    @Singleton
    internal fun provideFhgEndpoint(retrofitBuilder: Retrofit.Builder): FhgEndpoint {
        return retrofitBuilder.baseUrl(FhgEndpoint.BASE_URL)
            .build()
            .create(FhgEndpoint::class.java)
    }

    @Provides
    @Singleton
    internal fun provideUntisEndpoint(retrofitBuilder: Retrofit.Builder): UntisFhgEndpoint {
        return retrofitBuilder.baseUrl(UntisFhgEndpoint.BASE_URL)
            .build()
            .create(UntisFhgEndpoint::class.java)
    }

    @Provides
    @Singleton
    internal fun providesSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }
}