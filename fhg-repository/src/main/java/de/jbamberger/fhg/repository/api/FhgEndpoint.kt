package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.LiveData

import de.jbamberger.api.data.EventCalendar
import de.jbamberger.api.data.FeedChunk
import de.jbamberger.api.data.FeedItem
import de.jbamberger.fhg.repository.data.VPlanDay
import retrofit2.http.GET

/**
 * Interface that defines the different access routes to the https://fhg-radolfzell.de/ endpoint.
 *
 * The accessed values are the "Vertretungsplan":
 * {@code vertretungsplan/<page>/subst_001.htm}
 *
 * the wordpress json api:
 * {@code wp-json/wp/<version>/<content>}
 *
 * the wordpress RSS and Atom feeds:
 * {@code feed[/atom]}
 *
 * and the all-in-one calendar exports:
 * {@code ?plugin=all-in-one-event-calendar&<format options>}
 *
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal interface FhgEndpoint {
    companion object {
        const val BASE_URL = "https://fhg-radolfzell.de/"
    }

    @get:GET("vertretungsplan/f1/subst_001.htm")
    val vPlanFrame1: LiveData<ApiResponse<VPlanDay>>

    @get:GET("vertretungsplan/f2/subst_001.htm")
    val vPlanFrame2: LiveData<ApiResponse<VPlanDay>>

    @get:GET("/wp-json/wp/v2/posts")
    val feed: LiveData<ApiResponse<List<FeedItem>>>

    @get:GET("feed")
    val xmlFeed: LiveData<ApiResponse<FeedChunk>>

    @get:GET("feed/atom")
    val atomFeed: LiveData<ApiResponse<FeedChunk>>

    @get:GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&no_html=true")
    val icsCalendar: LiveData<ApiResponse<EventCalendar>>

    @get:GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&xml=true")
    val xmlCalendar: LiveData<ApiResponse<EventCalendar>>
}
