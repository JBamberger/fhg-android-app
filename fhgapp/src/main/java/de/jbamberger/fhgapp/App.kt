package de.jbamberger.fhgapp

import android.app.Application
import android.util.Log
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import de.jbamberger.fhg.repository.RepoHelper
import de.jbamberger.fhgapp.di.AppInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class App : Application(), HasAndroidInjector {

    @Inject
    internal lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var repoHelper: RepoHelper

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        AppInjector.init(this)
    }

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return dispatchingAndroidInjector
    }


    /**
     * A tree which logs important information for crash reporting.
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            //FIXME: use reporting library
            Log.e(tag, message, t)
        }
    }
}
