package de.jbamberger.fhgapp.ui.about.license

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhgapp.repository.util.AppExecutors
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LicenseViewModel
@Inject internal constructor(
    app: Application,
    executors: AppExecutors,
    dependencyReader: DependencyReader
) :
    AndroidViewModel(app) {

    private val _dependencies = MutableLiveData<List<DependencyInformation>>()

    val dependencies: LiveData<List<DependencyInformation>>
        get() = _dependencies

    init {
        executors.diskIO().execute {
            try {
                val dependencyList = dependencyReader.getDependencies()
                executors.mainThread().execute {
                    _dependencies.value = dependencyList
                }
            } catch (e: DependencyReadingException) {
                Timber.e(e, "Failed to load dependency list.")
                throw NotImplementedError(
                    e.message ?: "Failed to load dep. list. Error message missing."
                )
            }
        }
    }
}