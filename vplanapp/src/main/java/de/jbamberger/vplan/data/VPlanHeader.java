package de.jbamberger.vplan.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class VPlanHeader extends VPlanElement {
    @SerializedName("http_last_updated")
    public long lastUpdated = 0;

    @SerializedName("title")
    public String title = "Noch keine Daten geladen!";

    @SerializedName("status")
    public String status = "";

    @SerializedName("message_of_the_day")
    public String motd = "";

    public VPlanHeader() {
        super(VPlanElement.TYPE_HEADER);
    }

    public String getMotd() {
        return motd;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
