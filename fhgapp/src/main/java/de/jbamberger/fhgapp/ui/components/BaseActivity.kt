package de.jbamberger.fhgapp.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class BaseActivity<T : ViewModel> : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: T

    abstract val viewModelClass: Class<T>


    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
    }
}
