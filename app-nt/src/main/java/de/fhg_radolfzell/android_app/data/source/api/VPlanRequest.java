package de.fhg_radolfzell.android_app.data.source.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanRequest {
    @SerializedName("grades")
    String[] grades;

    public VPlanRequest(String[] grades) {
        this.grades = grades;
    }
}
