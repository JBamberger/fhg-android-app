package de.fhg_radolfzell.android_app.data.source;

import de.fhg_radolfzell.android_app.data.Feed;
import de.fhg_radolfzell.android_app.data.Post;
import de.fhg_radolfzell.android_app.data.RSS;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author Jannik
 * @version 28.07.2016.
 */
public interface FhgWebInterface {

    //JSON REST API : https://fhg-radolfzell.de/wp-json/wp/v2/posts

    String BASE_URL = "https://fhg-radolfzell.de/";

    @GET("vertretungsplan/f1/subst_001.htm")
    Call<String> vPlanFrame1();

    @GET("vertretungsplan/f2/subst_001.htm")
    Call<String> vPlanFrame2();

    @GET("/wp-json/wp/v2/posts")
    Call<Post[]> getFeed();

    @GET("feed")
    Call<RSS> getXMLFeed();

    @GET("feed/atom")
    Call<Feed> getAtomFeed();

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&no_html=true")
    Call<ResponseBody> getICSCalendar();

    @GET("?plugin=all-in-one-event-calendar&controller=ai1ec_exporter_controller&action=export_events&xml=true")
    Call<String> getXMLCalendar();

}
