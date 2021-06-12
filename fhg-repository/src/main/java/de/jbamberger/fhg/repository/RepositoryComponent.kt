package de.jbamberger.fhg.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import dagger.*
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.api.FhgTypeConverterFactory
import de.jbamberger.fhg.repository.api.LiveDataCallAdapter
import de.jbamberger.fhg.repository.api.UntisFhgEndpoint
import de.jbamberger.fhg.repository.util.FeedMediaLoader
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
class RepoHelper internal constructor(internal val component: RepositoryComponent) {
    fun provideFeedMediaLoaderFactory(): FeedMediaLoader.Factory {
        return component.provideFeedMediaLoaderFactory()
    }
}

@Module
class RepoInitModule {

    @Singleton
    @Provides
    fun providesRepoHelper(app: Application): RepoHelper {
        return RepoHelper(DaggerRepositoryComponent.builder().application(app).build())
    }

    @Singleton
    @Provides
    fun provideRepository(helper: RepoHelper): Repository {
        return helper.component.provideRepository()
    }
}

@Singleton
@Component(modules = [RepoBindingModule::class])
internal interface RepositoryComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): RepositoryComponent
    }

    fun provideRepository(): Repository
    fun provideFeedMediaLoaderFactory(): FeedMediaLoader.Factory

}

@Module(includes = [RepoInstantiationModule::class])
internal abstract class RepoBindingModule {

    @Binds
    @Singleton
    internal abstract fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    @Singleton
    internal abstract fun bindsContext(app: Application): Context
}

@Module
internal class RepoInstantiationModule {

    companion object {
        private const val DB_NAME = "fhg-db.sglite"
        private const val CACHE_SIZE = 10 * 1024 * 1024.toLong() //10 MiB
    }

    @Provides
    @Singleton
    internal fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpCache(context: Context): Cache {
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

    @Provides
    @Singleton
    internal fun providesFeedMediaLoaderFactory(): FeedMediaLoader.Factory {
        return FeedMediaLoader.Factory()
    }
}