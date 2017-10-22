package de.fhg_radolfzell.android_app.data.source;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.main.vplan.CourseSetting;
import de.fhg_radolfzell.android_app.main.vplan.VPlanScope;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static de.jbamberger.util.Preconditions.checkNotNull;


@VPlanScope
public class VPlanHtmlDataSource implements VPlanDataSource {

    private final FhgWebInterface mEndpoint;
    private final VPlanHtmlParser mParser;
    private VPlan[] mVPlan;

    @Inject
    public VPlanHtmlDataSource(@NonNull FhgWebInterface endpoint, @NonNull VPlanHtmlParser parser) {
        this.mEndpoint = checkNotNull(endpoint, "Endpoint can not be null");
        this.mParser = checkNotNull(parser, "Parser can not be null");
        this.mVPlan = new VPlan[2];
    }

    /**
     * FIXME: completely broken (encoding, concurrency, ignores grades)
     */
    @Override
    public void loadVPlan(final LoadVPlanCallback callback) {
        mEndpoint.vPlanFrame1().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        mVPlan[0] = mParser.parseVPlan(response.body());
                    } catch (IOException e) {
                        Timber.d(e);
                        callback.onVPlanNotAvailable();
                    }

                    if(mVPlan[1] != null) {
                        callback.onVPlanLoaded(mVPlan);
                        mVPlan = new VPlan[2];
                    }
                } else {
                    callback.onVPlanNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onVPlanNotAvailable();
            }
        });

        mEndpoint.vPlanFrame2().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        mVPlan[1] = mParser.parseVPlan(response.body());
                    } catch (IOException e) {
                        Timber.d(e);
                        callback.onVPlanNotAvailable();
                    }

                    if(mVPlan[0] != null) {
                        callback.onVPlanLoaded(mVPlan);
                        mVPlan = new VPlan[2];
                    }
                } else {
                    callback.onVPlanNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onVPlanNotAvailable();
            }
        });
    }

    @Override
    public void loadVPlan(@NonNull String[] grades, @NonNull LoadVPlanCallback callback) {
        loadVPlan(callback);
    }

    @Override
    public void loadVPlan(@NonNull CourseSetting[] courses, @NonNull LoadVPlanCallback callback) {
        loadVPlan(callback);
    }
}
