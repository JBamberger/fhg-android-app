package de.jbamberger.api

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Singleton
@Component(modules = [(FhgApiModule::class), (NetModule::class)])
interface FhgApiComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): FhgApiComponent
    }

    fun inject(builder: FhgApi.Provider)
}
