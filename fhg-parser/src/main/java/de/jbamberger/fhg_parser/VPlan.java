package de.jbamberger.fhg_parser;

import android.support.annotation.NonNull;

import java.util.List;

import static de.jbamberger.util.Preconditions.checkNotNull;

public final class VPlan {

    @NonNull
    private final String dateAndDay;

    @NonNull
    private final String lastUpdated;

    @NonNull
    private final String motd;

    @NonNull
    private final List<VPlanRow> vPlanRows;

    VPlan(@NonNull String dateAndDay, @NonNull String lastUpdated, @NonNull String motd, @NonNull List<VPlanRow> vPlanRows) {
        this.dateAndDay = checkNotNull(dateAndDay);
        this.lastUpdated = checkNotNull(lastUpdated);
        this.motd = checkNotNull(motd);
        this.vPlanRows = checkNotNull(vPlanRows);
    }

    @NonNull
    public String getDateAndDay() {
        return dateAndDay;
    }

    @NonNull
    public String getLastUpdated() {
        return lastUpdated;
    }

    @NonNull
    public String getMotd() {
        return motd;
    }

    @NonNull
    public List<VPlanRow> getVPlanRows() {
        return vPlanRows;
    }

    @Override
    public String toString() {
        return "VPlan{" +
                "dateAndDay='" + dateAndDay + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", motd='" + motd + '\'' +
                ", vPlanRows=" + vPlanRows +
                '}';
    }
}
