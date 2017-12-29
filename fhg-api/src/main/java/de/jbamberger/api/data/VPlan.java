package de.jbamberger.api.data;

import android.support.annotation.NonNull;

import static de.jbamberger.util.Preconditions.checkNotNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public final class VPlan {

    public static final class Builder {

        private VPlanDay day1;
        private VPlanDay day2;

        public synchronized void addDay1(@NonNull VPlanDay day1) {
            this.day1 = day1;
        }

        public synchronized void addDay2(@NonNull VPlanDay day2) {
            this.day2 = day2;
        }

        public synchronized VPlan build() {
            checkNotNull(day1);
            checkNotNull(day2);
            return new VPlan(day1, day2);
        }
    }

    @NonNull
    private final VPlanDay day1;
    @NonNull
    private final VPlanDay day2;

    private VPlan(@NonNull VPlanDay day1, @NonNull VPlanDay day2) {
        this.day1 = day1;
        this.day2 = day2;
    }

    @NonNull
    public VPlanDay getDay1() {
        return day1;
    }

    @NonNull
    public VPlanDay getDay2() {
        return day2;
    }
}
