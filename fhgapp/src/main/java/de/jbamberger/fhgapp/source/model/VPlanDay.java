package de.jbamberger.fhgapp.source.model;

import android.support.annotation.NonNull;

import de.jbamberger.fhg_parser.VPlan;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanDay {

    @NonNull
    private final VPlan plan;
    private final long httpLastUpdated;

    public VPlanDay(@NonNull VPlan plan, long httpLastUpdated) {
        this.plan = plan;
        this.httpLastUpdated = httpLastUpdated;
    }

    @NonNull
    public VPlan getPlan() {
        return plan;
    }

    public long getHttpLastUpdated() {
        return httpLastUpdated;
    }
}
