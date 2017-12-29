package de.jbamberger.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.jbamberger.api.data.VPlan;
import de.jbamberger.api.data.VPlanDay;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FhgApiImpl implements FhgApi {

    private FhgEndpoint endpoint;

    @Inject
    FhgApiImpl(@NonNull Context context) {
        this.endpoint = NetModule.getEndpoint(context);
    }

    @Override
    public LiveData<ApiResponse<VPlan>> getVPlan() {
        LiveData<ApiResponse<VPlanDay>> day1 = endpoint.getVPlanFrame1();
        LiveData<ApiResponse<VPlanDay>> day2 = endpoint.getVPlanFrame2();
        AtomicBoolean isLoaded = new AtomicBoolean(false);
        final VPlan.Builder builder = new VPlan.Builder();
        final MediatorLiveData<ApiResponse<VPlan>> merger = new MediatorLiveData<>();
        merger.addSource(day1, response -> {
            merger.removeSource(day1); // there is only one value, i.e. we don't need this anymore

            if (response != null && response.isSuccessful() && response.body != null) {
                builder.addDay1(response.body);
                if (isLoaded.getAndSet(true)) {
                    merger.setValue(new ApiResponse<>(builder.build(), response));
                }
            } else {
                merger.removeSource(day2);
                if (response != null) {
                    merger.setValue(new ApiResponse<>(new Throwable(response.errorMessage)));
                } else {
                    merger.setValue(new ApiResponse<>(new Throwable("Network error")));
                }
            }
        });
        merger.addSource(day2, response -> {
            merger.removeSource(day2); // there is only one value, i.e. we don't need this anymore

            if (response != null && response.isSuccessful() && response.body != null) {
                builder.addDay2(response.body);
                if (isLoaded.getAndSet(true)) {
                    merger.setValue(new ApiResponse<>(builder.build(), response));
                }
            } else {
                merger.removeSource(day1);
                if (response != null) {
                    merger.setValue(new ApiResponse<>(new Throwable(response.errorMessage)));
                } else {
                    merger.setValue(new ApiResponse<>(new Throwable("Network error")));
                }
            }
        });

        return merger;
    }
}
