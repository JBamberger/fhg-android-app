package de.jbamberger.fhg.repository.api

import android.arch.lifecycle.LiveData
import de.jbamberger.fhg.repository.data.EventCalendar
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.FeedMedia
import de.jbamberger.fhg.repository.data.VPlanDay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("vertretungsplan/f1/subst_001.htm")
    fun getVPlanFrame1(): LiveData<ApiResponse<VPlanDay>>

    @GET("vertretungsplan/f2/subst_001.htm")
    fun getVPlanFrame2(): LiveData<ApiResponse<VPlanDay>>

    @GET("/wp-json/wp/v2/posts")
    fun getFeed(): LiveData<ApiResponse<List<FeedItem>>>

    @GET("/wp-json/wp/v2/posts")
    fun getFeedPaged(@Query(value = "page") page: Int): Call<List<FeedItem>>

    @GET("/wp-json/wp/v2/posts")
    fun getFeedPage(
            @Query(value = "before") before: String? = null,
            @Query(value = "after") after: String? = null,
            @Query(value = "per_page") count: Int? = null,
            @Query(value = "order") order: String? = "desc"): Call<List<FeedItem>>

    @GET("/wp-json/wp/v2/media/{mediaId}")
    fun getFeedMedia(@Path("mediaId") mediaId: Int): Call<FeedMedia>

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&no_html=true")
    fun getIcsCalendar(): LiveData<ApiResponse<EventCalendar>>

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&xml=true")
    fun getXmlCalendar(): LiveData<ApiResponse<EventCalendar>>
}
