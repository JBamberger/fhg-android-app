package de.jbamberger.fhgapp.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jbamberger.fhgapp.AppExecutors;
import de.jbamberger.fhgapp.source.model.VPlan;
import de.jbamberger.fhgapp.source.model.VPlanDay;
import de.jbamberger.fhgapp.util.AbsentLiveData;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
public class Repository {

    private final FhgApi api;
    private final AppExecutors appExecutors;
    private final MutableLiveData<Resource<VPlan>> posts = new MutableLiveData<>();


    @Inject
    public Repository(AppExecutors appExecutors, FhgApi api) {
        this.appExecutors = appExecutors;
        this.api = api;
    }

    public LiveData<Resource<VPlan>> getVPlan() {
        return new NetworkBoundResource<VPlan, VPlan>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull VPlan item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable VPlan data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<VPlan> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VPlan>> createCall() {
                LiveData<ApiResponse<VPlanDay>> day1 = api.getVPlanFrame1();
                LiveData<ApiResponse<VPlanDay>> day2 = api.getVPlanFrame2();
                AtomicBoolean fin = new AtomicBoolean(false);
                final VPlan.Builder builder = new VPlan.Builder();
                final MediatorLiveData<ApiResponse<VPlan>> merger = new MediatorLiveData<>();
                merger.addSource(day1, response -> {
                    if (response != null && response.isSuccessful()) {
                        builder.addDay1(response.body);
                        if (fin.compareAndSet(true, true)) {
                            merger.setValue(new ApiResponse<VPlan>(builder.build(), response));
                        }
                    } else {
                        merger.removeSource(day2);
                        if (response != null) {
                            merger.setValue(new ApiResponse<>(new Throwable(response.errorMessage)));
                        } else {
                            merger.setValue(new ApiResponse<>(new Throwable("Network error")));
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
                            merger.setValue(new ApiResponse<>(new Throwable(response.errorMessage)));
                        } else {
                            merger.setValue(new ApiResponse<>(new Throwable("Network error")));
                        }
                    }
                    merger.removeSource(day2);
                });

                return merger;
            }
        }.asLiveData();
    }
}
