package de.jbamberger.api;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface FhgApi {

    class Builder {
        public static FhgApi getInstance(@NonNull Context context) {
            return new FhgApiImpl(context);
        }
    }

    LiveData<ApiResponse<VPlan>> getVPlan();
}
