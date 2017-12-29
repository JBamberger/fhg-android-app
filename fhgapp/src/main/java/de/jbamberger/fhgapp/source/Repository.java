package de.jbamberger.fhgapp.source;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jbamberger.api.ApiResponse;
import de.jbamberger.api.FhgApi;
import de.jbamberger.api.data.VPlan;
import de.jbamberger.fhgapp.AppExecutors;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
public class Repository {

    private final FhgApi api;
    private final AppExecutors appExecutors;
    private VPlan plan;

    @Inject
    public Repository(AppExecutors appExecutors, FhgApi api) {
        this.appExecutors = appExecutors;
        this.api = api;
    }

    public LiveData<Resource<VPlan>> getVPlan() {
        return new NetworkBoundResource<VPlan, VPlan>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull VPlan item) {
                plan = item;
                Timber.d("saveCallResult()");
            }

            @Override
            protected boolean shouldFetch(@Nullable VPlan data) {
                Timber.d("shouldFetch()");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<VPlan> loadFromDb() {
                Timber.d("loadFromDb()");
                MutableLiveData<VPlan> l = new MutableLiveData<>();
                l.setValue(plan);
                return l;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VPlan>> createCall() {
                Timber.d("createCall()");
                MediatorLiveData<ApiResponse<VPlan>> m = new MediatorLiveData<>();
                m.addSource(api.getVPlan(), (x) -> {
                    Timber.d("received vplan %s", x);
                    m.setValue(x);
                });
                return m;
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("onFetchFailed()");
            }
        }.asLiveData();
    }
}
