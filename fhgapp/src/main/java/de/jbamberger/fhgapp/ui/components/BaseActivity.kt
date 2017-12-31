package de.jbamberger.fhgapp.ui.components

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    @JvmField
    internal var fragmentInjector: DispatchingAndroidInjector<Fragment>? = null

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
