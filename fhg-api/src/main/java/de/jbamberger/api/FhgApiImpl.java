package de.jbamberger.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class FhgApiImpl implements FhgApi {

    private FhgEndpoint endpoint;

    FhgApiImpl(@NonNull Context context) {
        this.endpoint = NetModule.getEndpoint(context);
    }

    @Override
    public LiveData<ApiResponse<VPlan>> getVPlan() {
        LiveData<ApiResponse<VPlanDay>> day1 = endpoint.getVPlanFrame1();
        LiveData<ApiResponse<VPlanDay>> day2 = endpoint.getVPlanFrame2();
        AtomicBoolean fin = new AtomicBoolean(false);
        final VPlan.Builder builder = new VPlan.Builder();
        final MediatorLiveData<ApiResponse<VPlan>> merger = new MediatorLiveData<>();
        merger.addSource(day1, response -> {
            if (response != null && response.isSuccessful()) {
                builder.addDay1(response.body);
                if (fin.compareAndSet(true, true)) {
                    merger.setValue(new ApiResponse<>(builder.build(), response));
                }
            } else {
                merger.removeSource(day2);
                if (response != null) {
                    merger.setValue(new ApiResponse<VPlan>(new Throwable(response.errorMessage)));
                } else {
                    merger.setValue(new ApiResponse<VPlan>(new Throwable("Network error")));
                }
            }
            merger.removeSource(day1);
        });
        merger.addSource(day2, response -> {
            if (response != null && response.isSuccessful()) {
                builder.addDay2(response.body);
                if (fin.compareAndSet(true, true)) {
                    merger.setValue(new ApiResponse<>(builder.build(), response));
                }
            } else {
                merger.removeSource(day1);
                if (response != null) {
                    merger.setValue(new ApiResponse<VPlan>(new Throwable(response.errorMessage)));
                } else {
                    merger.setValue(new ApiResponse<VPlan>(new Throwable("Network error")));
                }
            }
            merger.removeSource(day2);
        });

        return merger;
    }
}
