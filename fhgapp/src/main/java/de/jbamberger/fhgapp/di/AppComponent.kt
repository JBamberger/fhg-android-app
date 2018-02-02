package de.jbamberger.fhgapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.jbamberger.fhgapp.App
import javax.inject.Singleton

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Singleton
@Component(modules = [
    AppModule::class,
    BuildersModule::class,
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}