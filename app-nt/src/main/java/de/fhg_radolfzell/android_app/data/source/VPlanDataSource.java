package de.fhg_radolfzell.android_app.data.source;

import android.support.annotation.NonNull;

import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.main.vplan.CourseSetting;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface VPlanDataSource {
    interface LoadVPlanCallback {
        void onVPlanLoaded(@NonNull VPlan[] vPlan);

        void onVPlanNotAvailable();
    }

    void loadVPlan(@NonNull LoadVPlanCallback callback);
    void loadVPlan(@NonNull String[] grades, @NonNull LoadVPlanCallback callback);
    void loadVPlan(@NonNull CourseSetting[] courses, @NonNull LoadVPlanCallback callback);

}
