package de.jbamberger.fhgapp.ui.about.license

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import de.jbamberger.fhgapp.App
import de.jbamberger.fhgapp.repository.util.AppExecutors
import okio.*
import javax.inject.Inject
import kotlin.io.use


@HiltViewModel
class LicenseViewModel
@Inject internal constructor(app: Application, executors: AppExecutors, moshi: Moshi) :
    AndroidViewModel(app) {

    private val _dependencies = MutableLiveData<List<LicenseeArtifactInfo>>()

    val dependencies: LiveData<List<LicenseeArtifactInfo>>
        get() = _dependencies

    init {
        executors.diskIO().execute {
            val jsonAdapter = moshi.adapter<List<LicenseeArtifactInfo>>(
                Types.newParameterizedType(
                    List::class.java,
                    LicenseeArtifactInfo::class.java
                )
            )
            try {
                getApplication<App>().assets.open("artifacts.json").source().buffer().use {
                    val depsList = jsonAdapter.fromJson(it) ?: emptyList()
                    executors.mainThread().execute {
                        _dependencies.value = depsList
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}