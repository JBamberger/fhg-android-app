package de.jbamberger.fhgapp.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jbamberger.api.data.VPlanDay;
import de.jbamberger.fhgapp.AppExecutors;
import de.jbamberger.fhgapp.source.model.VPlanSet;
import de.jbamberger.fhgapp.util.AbsentLiveData;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
public class Repository {

    private final FhgApi api;
    private final AppExecutors appExecutors;
    private final MutableLiveData<Resource<VPlanSet>> posts = new MutableLiveData<>();


    @Inject
    public Repository(AppExecutors appExecutors, FhgApi api) {
        this.appExecutors = appExecutors;
        this.api = api;
    }

    public LiveData<Resource<VPlanSet>> getVPlan() {
        return new NetworkBoundResource<VPlanSet, VPlanSet>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull VPlanSet item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable VPlanSet data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<VPlanSet> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VPlanSet>> createCall() {
                LiveData<ApiResponse<VPlanDay>> day1 = api.getVPlanFrame1();
                LiveData<ApiResponse<VPlanDay>> day2 = api.getVPlanFrame2();
                AtomicBoolean fin = new AtomicBoolean(false);
                final VPlanSet.Builder builder = new VPlanSet.Builder();
                final MediatorLiveData<ApiResponse<VPlanSet>> merger = new MediatorLiveData<>();
                merger.addSource(day1, response -> {
                    Timber.e("Day one Response: %s", response);
                    if (response != null && response.isSuccessful()) {
                        long lastModified = 0;
                        if (response.headers != null) {
                            //lastModified = Long.parseLong(response.headers.get("Last-Modified"));
                        }
                        builder.addDay1(new de.jbamberger.fhgapp.source.model.VPlanDay(response.body, lastModified));
                        if (fin.compareAndSet(true, true)) {
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
                    merger.removeSource(day1);
                });
                merger.addSource(day2, response -> {
                    Timber.e("Day one Response: %s", response);
                    if (response != null && response.isSuccessful()) {
                        long lastModified = 0;
                        if (response.headers != null) {
                            //lastModified = Long.parseLong(response.headers.get("Last-Modified"));
                        }
                        builder.addDay2(new de.jbamberger.fhgapp.source.model.VPlanDay(response.body, lastModified));
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
