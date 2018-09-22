package de.jbamberger.fhg.repository

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.*
import de.jbamberger.fhg.repository.api.FhgApiModule
import de.jbamberger.fhg.repository.api.FhgEndpoint
import de.jbamberger.fhg.repository.db.AppDatabase
import de.jbamberger.fhg.repository.util.FeedMediaLoaderFactory
import okhttp3.OkHttpClient
import javax.inject.Singleton


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class RepoHelper internal constructor(internal val component: RepositoryComponent) {
    fun provideFeedMediaLoaderFactory(): FeedMediaLoaderFactory {
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
@Component(modules = [RepositoryModule::class])
internal interface RepositoryComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): RepositoryComponent
    }

    fun provideRepository(): Repository
    fun provideFeedMediaLoaderFactory(): FeedMediaLoaderFactory

}

@Module(includes = [FhgApiModule::class])
internal abstract class RepositoryModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        internal fun providesSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @JvmStatic
        @Provides
        @Singleton
        internal fun provideAppDatabase(application: Application): AppDatabase {
            return Room.databaseBuilder(application, AppDatabase::class.java, "fhg-db.sglite")
                    .build()
        }

        @JvmStatic
        @Provides
        @Singleton
        internal fun providesFeedMediaLoaderFactory(
                api: FhgEndpoint, httpClient: OkHttpClient): FeedMediaLoaderFactory {
            return FeedMediaLoaderFactory(api, httpClient)
        }
    }

    @Binds
    @Singleton
    internal abstract fun bindRepository(impl: RepositoryImpl): Repository
}