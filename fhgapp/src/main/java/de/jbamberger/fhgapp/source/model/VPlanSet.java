package de.jbamberger.fhgapp.source.model;

import android.support.annotation.NonNull;

import static de.jbamberger.util.Preconditions.checkNotNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanSet {

    public static class Builder {

        private VPlanDay day1;
        private VPlanDay day2;

        public Builder() {

        }

        public Builder addDay1(VPlanDay day1) {
            this.day1 = day1;
            return this;
        }

        public Builder addDay2(VPlanDay day2) {
            this.day2 = day2;
            return this;
        }

        public VPlanSet build() {
            checkNotNull(day1);
            checkNotNull(day2);
            return new VPlanSet(day1, day2);
        }
    }

    @NonNull
    private final VPlanDay day1;
    @NonNull
    private final VPlanDay day2;

    private VPlanSet(@NonNull VPlanDay day1, @NonNull VPlanDay day2) {
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
