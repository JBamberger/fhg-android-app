package xyz.jbapps.vplanapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VPlanData {

    private static final String TAG = "VPlanData";

    @SerializedName("vplan_header_row")
    private VPlanRow VPlanHeaderRow;
    @SerializedName("vplan_rows")
    private List<VPlanRow> mVPlanRows = new ArrayList<>();
    @SerializedName("vplan_header")
    private VPlanHeader vPlanHeader;


    public VPlanData() {
        vPlanHeader = new VPlanHeader();
    }

    public void addVPlanRow(VPlanRow vPlanRow) {
        mVPlanRows.add(vPlanRow);
    }

    public VPlanRow getVPlanRowAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        return mVPlanRows.get(position);
    }

    public int getVPlanRowCount() {
        return mVPlanRows.size();
    }

    public long getLastUpdated() {
        return vPlanHeader.lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.vPlanHeader.lastUpdated = lastUpdated;
    }

    public String getMotd() {
        return vPlanHeader.motd;
    }

    public void setMotd(String motd) {
        this.vPlanHeader.motd = motd;
    }

    public String getStatus() {
        return vPlanHeader.status;
    }

    public void setStatus(String status) {
        this.vPlanHeader.status = status;
    }

    public String getTitle() {
        return vPlanHeader.title;
    }

    public void setTitle(String title) {
        this.vPlanHeader.title = title;
    }

    public VPlanRow getVPlanHeaderRow() {
        return VPlanHeaderRow;
    }

    public void setVPlanHeaderRow(VPlanRow VPlanHeaderRow) {
        this.VPlanHeaderRow = VPlanHeaderRow;
    }

    public VPlanHeader getvPlanHeader() {
        return vPlanHeader;
    }

    public void setvPlanHeader(VPlanHeader vPlanHeader) {
        this.vPlanHeader = vPlanHeader;
    }
}
