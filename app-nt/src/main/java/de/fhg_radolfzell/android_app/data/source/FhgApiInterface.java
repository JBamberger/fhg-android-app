package de.fhg_radolfzell.android_app.data.source;

import de.fhg_radolfzell.android_app.data.source.api.GradeSubscription;
import de.fhg_radolfzell.android_app.data.source.api.RemoveSubscritption;
import de.fhg_radolfzell.android_app.data.source.api.VPlanRequest;
import de.fhg_radolfzell.android_app.data.CalendarEvent;
import de.fhg_radolfzell.android_app.data.VPlan;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Jannik
 * @version 28.07.2016.
 */
public interface FhgApiInterface {

    String BASE_URL = "https://app.fhg-radolfzell.de/api/";

    @POST("students/plan") //TODO grades already converted
    Call<VPlan[]> getStudentVPlan(@Body VPlanRequest grades);

    @POST("students/plan") //TODO grades already converted
    Call<VPlan[]> getCompleteStudentVPlan();

    @GET("calendar")
    Call<CalendarEvent[]> getCalendar();

    @POST("device/subscriptions")
    Call<Object> getSubscritpions();

    /**
     *
     * @return null
     */
    @POST("device/subscribe")
    Call<Object> subscribe(@Body GradeSubscription subscription);

    /**
     * Delete all subscriptions of token.
     * @param token will no longer be notified
     * @return null
     */
    @POST("device/unsubscribe")
    Call<Object> unsubscribe(@Body RemoveSubscritption token); //TODO: type

    /**
     * API v1:
     * Content-Type: application/json
     * Content-Encoding: utf-8
     *
     *
     *
     * api/device/subscribe         input: {token:string, grades:[string]}
     *                              output: {?}
     *
     * api/device/unsubscribe       input: {token:string}
     *                              output: {?}
     *
     * api/device/subscriptions     input: {token:string}
     *                              output: {error:String, subscriptions:[String]}
     *
     * api/students/vplan           input: {grades:[String]}
     *                              output: [{etag:string, date_at:String, updated_at:String, motd:String, data:[{grade:String, subject:String, description:String, hour:Integer, room:String, omitted:Integer}]}]
     *
     * api/calendar                 input: {?}
     *                              output: [{id:Integer, name:String, description:String, start_at:String, end_at:String, url:String, categories:String}]
     *
     *
     * fcm Message                  {registration_ids:[Integer], collapse_key:String, data:{id:Integer, etag:String, date_at:String}}
     *
     * */



}
