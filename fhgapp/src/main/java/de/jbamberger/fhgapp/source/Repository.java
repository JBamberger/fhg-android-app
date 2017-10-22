package de.jbamberger.fhgapp.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jbamberger.api.ApiResponse;
import de.jbamberger.api.FhgApi;
import de.jbamberger.api.data.VPlan;
import de.jbamberger.fhgapp.AppExecutors;
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
                return api.getVPlan();
            }
        }.asLiveData();
    }
}
