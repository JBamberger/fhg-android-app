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
import de.jbamberger.api.data.FeedChunk;
import de.jbamberger.api.data.VPlan;
import de.jbamberger.fhgapp.AppExecutors;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Singleton
public class Repository {

    private final FhgApi api;
    private final AppExecutors appExecutors;
    private VPlan plan;
    private FeedChunk feed;

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
            }

            @Override
            protected boolean shouldFetch(@Nullable VPlan data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<VPlan> loadFromDb() {
                MutableLiveData<VPlan> l = new MutableLiveData<>();
                l.setValue(plan);
                return l;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VPlan>> createCall() {
                MediatorLiveData<ApiResponse<VPlan>> m = new MediatorLiveData<>();
                m.addSource(api.getVPlan(), m::setValue);
                return m;
            }
        }.asLiveData();
    }

    public LiveData<Resource<FeedChunk>> getFeed() {
        return new NetworkBoundResource<FeedChunk, FeedChunk>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull FeedChunk item) {
                feed = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable FeedChunk data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<FeedChunk> loadFromDb() {
                MutableLiveData<FeedChunk> l = new MutableLiveData<>();
                l.setValue(feed);
                return l;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<FeedChunk>> createCall() {
                MediatorLiveData<ApiResponse<FeedChunk>> m = new MediatorLiveData<>();
                m.addSource(api.getFeed(), m::setValue);
                return m;
            }
        }.asLiveData();
    }
}
