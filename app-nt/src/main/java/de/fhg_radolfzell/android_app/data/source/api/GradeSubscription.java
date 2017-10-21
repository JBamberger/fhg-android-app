package de.fhg_radolfzell.android_app.data.source.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class GradeSubscription {
    @SerializedName("token")
    String token;
    @SerializedName("grades")
    String[] grades;

    public GradeSubscription(String token, String[] grades) {
        this.token = token;
        this.grades = grades;
    }
}
