package xyz.jbapps.vplanapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VPlanData {

    private static final String TAG = "VPlanData";


    @SerializedName("last_updated")
    private long lastUpdated = 0;
    @SerializedName("vplan_header")
    private VPlanRow VPlanHeaderRow;
    @SerializedName("vplan_rows")
    private List<VPlanRow> mVPlanRows = new ArrayList<>();
    @SerializedName("title_day")
    private String title;
    @SerializedName("last_updated")
    private String status;
    @SerializedName("message_of_the_day")
    private String motd;


    public VPlanData() {

    }

    public void addVPlanRow(VPlanRow vPlanRow) {
        mVPlanRows.add(vPlanRow);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VPlanRow getVPlanHeaderRow() {
        return VPlanHeaderRow;
    }

    public void setVPlanHeaderRow(VPlanRow VPlanHeaderRow) {
        this.VPlanHeaderRow = VPlanHeaderRow;
    }



}
