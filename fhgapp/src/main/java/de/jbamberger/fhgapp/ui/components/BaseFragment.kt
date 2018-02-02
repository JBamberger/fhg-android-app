package de.jbamberger.fhgapp.ui.components

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import de.jbamberger.fhgapp.di.Injectable
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class BaseFragment<T : ViewModel?> : Fragment(), Injectable {

    @Inject
    @JvmField
    internal var viewModelFactory: ViewModelProvider.Factory? = null

    protected var viewModel: T? = null

    abstract val viewModelClass: Class<T>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory!!).get(viewModelClass)
    }
}