package de.jbamberger.fhg.repository

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
class RepoInitModule {

    @Inject
    internal lateinit var repo: Repository

    @Singleton
    @Provides
    fun provideRepository(app: Application): Repository {
        DaggerRepositoryComponent.builder().application(app).build().inject(this)

        return repo
    }
}