package de.jbamberger.api;

import android.arch.lifecycle.LiveData;

import retrofit2.Response;
import retrofit2.http.GET;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

interface FhgEndpoint {


    //JSON REST API : https://fhg-radolfzell.de/wp-json/wp/v2/posts

    String BASE_URL = "https://fhg-radolfzell.de/";

    @GET("vertretungsplan/f1/subst_001.htm")
    LiveData<ApiResponse<VPlanDay>> getVPlanFrame1();

    @GET("vertretungsplan/f2/subst_001.htm")
    LiveData<ApiResponse<VPlanDay>> getVPlanFrame2();

    @GET("/wp-json/wp/v2/posts")
    LiveData<ApiResponse<Response>> getFeed();

    @GET("feed")
    LiveData<ApiResponse<Response>> getXmlFeed();

    @GET("feed/atom")
    LiveData<ApiResponse<Response>> getAtomFeed();

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&no_html=true")
    LiveData<ApiResponse<Response>> getIcsCalendar();

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&xml=true")
    LiveData<ApiResponse<Response>> getXmlCalendar();
}
