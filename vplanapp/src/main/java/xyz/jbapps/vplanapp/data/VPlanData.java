package xyz.jbapps.vplanapp.data;

import java.util.ArrayList;
import java.util.List;

public class VPlanData {

    private static final String TAG = "VPlanData";

    private long lastUpdated = 0;
    private VPlanRow VPlanHeaderRow;
    private List<VPlanRow> mVPlanRows = new ArrayList<>();

    private String title;
    private String status;
    private String motd;


    public VPlanData() {

    }

    public boolean serialize() {
        return false;
    }



}
