package de.jbamberger.api

import android.app.Application
import android.arch.lifecycle.LiveData
import de.jbamberger.api.data.FeedItem
import de.jbamberger.api.data.VPlan
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface FhgApi {

    class Provider(app: Application) {

        @Inject
        lateinit var api: FhgApi

        init {
            DaggerFhgApiComponent.builder().application(app).build().inject(this)
        }

    }

    val vPlan: LiveData<ApiResponse<VPlan>>

    val feed: LiveData<ApiResponse<List<FeedItem>>>
}
