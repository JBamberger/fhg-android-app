package de.jbamberger.api

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module
abstract class FhgApiModule {

    @Binds
    @Singleton
    internal abstract fun bindsFhgApi(impl: FhgApiImpl): FhgApi
}
