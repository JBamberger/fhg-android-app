package de.jbamberger.fhgapp.ui.vplan;

import android.support.annotation.NonNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanHeader {
    @NonNull
    public final String dateAndDay;

    @NonNull
    public final String lastUpdated;

    @NonNull
    public final String motd;

    public VPlanHeader(@NonNull String dateAndDay, @NonNull String lastUpdated, @NonNull String motd) {
        this.dateAndDay = dateAndDay;
        this.lastUpdated = lastUpdated;
        this.motd = motd;
    }
}
