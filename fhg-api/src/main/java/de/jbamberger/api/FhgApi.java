package de.jbamberger.api;

import android.arch.lifecycle.LiveData;

import de.jbamberger.api.data.VPlan;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface FhgApi {

    LiveData<ApiResponse<VPlan>> getVPlan();
}
