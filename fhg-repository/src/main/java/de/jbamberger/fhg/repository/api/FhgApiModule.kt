package de.jbamberger.fhg.repository.api

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module(includes = [NetModule::class])
internal abstract class FhgApiModule {

    @Binds
    @Singleton
    internal abstract fun bindsFhgApi(impl: FhgApiImpl): FhgApi

    @Binds
    @Singleton
    internal abstract fun bindsContext(app: Application): Context

}
