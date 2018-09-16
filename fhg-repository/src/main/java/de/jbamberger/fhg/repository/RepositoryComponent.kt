package de.jbamberger.fhg.repository

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
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
    fun provideFeedMediaLoaderFactory() : FeedMediaLoaderFactory {
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

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Singleton
@Component(modules = [
    RepositoryModule::class])
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

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module(includes = [FhgApiModule::class])
internal class RepositoryModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "fhg-db.sglite").build()
    }

    @Provides
    @Singleton
    fun providesFeedMediaLoaderFactory(api: FhgEndpoint, httpClient: OkHttpClient): FeedMediaLoaderFactory {
        return FeedMediaLoaderFactory(api, httpClient)
    }
}