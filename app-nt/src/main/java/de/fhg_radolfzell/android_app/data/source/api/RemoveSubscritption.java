package de.fhg_radolfzell.android_app.data.source.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class RemoveSubscritption {
    @SerializedName("token")
    String token;

    public RemoveSubscritption(String token) {
        this.token = token;
    }
}
