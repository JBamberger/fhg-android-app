package de.jbamberger.vplan.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VPlanData {

    @SerializedName("vplan_header_row")
    private VPlanRow VPlanHeaderRow;
    @SerializedName("vplan_rows")
    private List<VPlanRow> mVPlanRows = new ArrayList<>();
    @SerializedName("vplan_header")
    private VPlanHeader vPlanHeader;


    public VPlanData() {
        vPlanHeader = new VPlanHeader();
    }

    public VPlanRow getVPlanRowAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        return mVPlanRows.get(position);
    }

    public void setRows(List<VPlanRow> rows) {
        this.mVPlanRows = rows;
    }

    public int getVPlanRowCount() {
        return mVPlanRows.size();
    }

    public void deleteVPlanRow(int position) {
        mVPlanRows.remove(position);
    }

    public void setLastUpdated(long lastUpdated) {
        this.vPlanHeader.lastUpdated = lastUpdated;
    }

    public void setMotd(String motd) {
        this.vPlanHeader.motd = motd;
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

    public VPlanHeader getvPlanHeader() {
        return vPlanHeader;
    }

}
