package xyz.jbapps.vplan.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import xyz.jbapps.vplan.api.model.Feed;
import xyz.jbapps.vplan.api.model.VPlanFrame;

/**
 * Retrofit-style API declaration
 */
public interface FhgApi {

    public static final String BASEURL = "http://www.fhg-radolfzell.de/";

    @GET("/vertretungsplan/f1/subst_001.htm")
    Call<VPlanFrame> getVPlanFrame1();

    @GET("/vertretungsplan/f2/subst_001.htm")
    Call<VPlanFrame> getVPlanFrame2();

    @GET("/wp-json/wp/v2/posts")
    Call<Feed> getJSONFeed();

    @GET("/feed/atom/")
    Call<Feed> getAtomFeed();

    @GET("/feed/")
    Call<Feed> getRSSFeed();
}
