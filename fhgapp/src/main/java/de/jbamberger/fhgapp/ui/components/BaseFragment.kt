package de.jbamberger.fhgapp.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import de.jbamberger.fhgapp.di.Injectable
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

abstract class BaseFragment<T : ViewModel> : Fragment(), Injectable {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: T

    abstract val viewModelClass: Class<T>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
    }
}