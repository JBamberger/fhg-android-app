package de.jbamberger.fhgapp.source.model;

import android.support.annotation.NonNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanDay {

    @NonNull
    private final de.jbamberger.api.VPlanDay plan;
    private final long httpLastUpdated;

    public VPlanDay(@NonNull de.jbamberger.api.VPlanDay plan, long httpLastUpdated) {
        this.plan = plan;
        this.httpLastUpdated = httpLastUpdated;
    }

    @NonNull
    public de.jbamberger.api.VPlanDay getPlan() {
        return plan;
    }

    public long getHttpLastUpdated() {
        return httpLastUpdated;
    }
}
