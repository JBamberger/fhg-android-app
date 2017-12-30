package de.jbamberger.api

import android.arch.lifecycle.LiveData

import de.jbamberger.api.data.EventCalendar
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.api.data.VPlanDay
import retrofit2.http.GET

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgEndpoint {

    @get:GET("vertretungsplan/f1/subst_001.htm")
    val vPlanFrame1: LiveData<ApiResponse<VPlanDay>>

    @get:GET("vertretungsplan/f2/subst_001.htm")
    val vPlanFrame2: LiveData<ApiResponse<VPlanDay>>

    @get:GET("/wp-json/wp/v2/posts")
    val feed: LiveData<ApiResponse<FeedChunk>>

    @get:GET("feed")
    val xmlFeed: LiveData<ApiResponse<FeedChunk>>

    @get:GET("feed/atom")
    val atomFeed: LiveData<ApiResponse<FeedChunk>>

    @get:GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&no_html=true")
    val icsCalendar: LiveData<ApiResponse<EventCalendar>>

    @get:GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&xml=true")
    val xmlCalendar: LiveData<ApiResponse<EventCalendar>>

    companion object {
        //JSON REST API : https://fhg-radolfzell.de/wp-json/wp/v2/posts
        val BASE_URL = "https://fhg-radolfzell.de/"
    }
}
