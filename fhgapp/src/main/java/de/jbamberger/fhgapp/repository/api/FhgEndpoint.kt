package de.jbamberger.fhgapp.repository.api

import androidx.lifecycle.LiveData
import de.jbamberger.fhgapp.repository.data.FeedItem
import de.jbamberger.fhgapp.repository.data.FeedMedia
import de.jbamberger.fhgapp.repository.data.VPlanDay
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
        internal const val BASE_URL = "https://fhg-radolfzell.de/"
        private const val CALENDAR_BASE = "?plugin=all-in-one-event-calendar" +
                "&controller=ai1ec_exporter_controller" +
                "&action=export_events"
        private const val JSON_V2 = "/wp-json/wp/v2"
    }

    @GET("vertretungsplan/f1/subst_001.htm")
    fun getVPlanFrame1(): LiveData<ApiResponse<VPlanDay>>

    @GET("vertretungsplan/f2/subst_001.htm")
    fun getVPlanFrame2(): LiveData<ApiResponse<VPlanDay>>

    @GET("$JSON_V2/posts?context=embed")
    fun getFeedPage(
        @Query(value = "before") before: String? = null,
        @Query(value = "after") after: String? = null,
        @Query(value = "per_page") count: Int? = null,
        @Query(value = "order") order: String? = "desc"
    ): Call<List<FeedItem>>

    @GET("$JSON_V2/media/{mediaId}?context=embed")
    fun getFeedMedia(@Path("mediaId") mediaId: Int): Call<FeedMedia>

    @GET("$JSON_V2/media?context=embed")
    fun getFeedMediaElements(@Query("include") ids: String): Call<List<FeedMedia>>

    @GET("$JSON_V2/posts?context=embed")
    suspend fun getFeedPage2(
        @Query(value = "before") before: String? = null,
        @Query(value = "after") after: String? = null,
        @Query(value = "per_page") count: Int? = null,
        @Query(value = "order") order: String? = "desc"
    ): List<FeedItem>
    // Error:
    // {"code":"rest_invalid_param","message":"Ung\u00fcltige(r) Parameter: before","data":{"status":400,"params":{"before":"Ung\u00fcltiges Datum."},"details":{"before":{"code":"rest_invalid_date","message":"Ung\u00fcltiges Datum.","data":null}}}}

    @GET("$JSON_V2/media/{mediaId}?context=embed")
    suspend fun getFeedMedia2(@Path("mediaId") mediaId: Int): FeedMedia


//    @GET("$CALENDAR_BASE&no_html=true")
//    fun getIcsCalendar(): LiveData<ApiResponse<EventCalendar>>
//
//    @GET("$CALENDAR_BASE&xml=true")
//    fun getXmlCalendar(): LiveData<ApiResponse<EventCalendar>>
}
