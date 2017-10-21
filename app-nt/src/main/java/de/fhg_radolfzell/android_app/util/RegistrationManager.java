package de.fhg_radolfzell.android_app.util;

import android.os.Handler;

import com.squareup.otto.Bus;

import java.util.Set;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.data.source.api.GradeSubscription;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class RegistrationManager {
    private static final int INITIAL_BACKOFF = 5000;
    private static int backoff = INITIAL_BACKOFF; //10 seconds after first call

    private Handler backoffHandler;
    private Runnable backofRunnable;

    private Bus eventBus;
    private FhgApiInterface api;
    private Storage storage;

    @Inject
    public RegistrationManager(Bus eventBus, FhgApiInterface api, Storage storage) {
        this.eventBus = eventBus;
        this.api = api;
        this.storage = storage;
        this.backoffHandler = new Handler();
    }

    /**
     * True if the device is subscribed successfully, false otherwise.
     *
     * @return subscription status
     */
    public boolean isSubscribed() {
        return storage.getFcmSubscribed();
    }

    /**
     * Checks which grades to subscribe for and execute subscription operation.
     */
    public void subscribe() {
        String token = storage.getFcmToken(); //FIXME: check if token is present
        String[] grades = {"all"}; //TODO: use constant
        Set<String> gradesSet = storage.getSettingsVPlanSelectedGrades();
        if (!storage.getSettingsVPlanShowAllGrades() && gradesSet.size() > 0) {
            grades = gradesSet.toArray(grades);
        }
        Call<Object> call = api.subscribe(new GradeSubscription(token, grades));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    backoff = INITIAL_BACKOFF;
                    storage.setFcmSubscribed(true);
                    if (backofRunnable != null) {
                        backoffHandler.removeCallbacks(backofRunnable);
                    }
                } else {
                    storage.setFcmSubscribed(false);
                    retryWithExponentialBackoff();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Timber.e(t, "Failed to update subscription for fcm");
                storage.setFcmSubscribed(false);
                retryWithExponentialBackoff();

            }
        });
    }

    private void retryWithExponentialBackoff() {
        backoff = 2 * backoff;
        backofRunnable = new Runnable() {
            @Override
            public void run() {
                subscribe();
            }
        };
        backoffHandler.postDelayed(backofRunnable, backoff);
    }

    /**
     * Call unsubscribe procedure.
     */
    public void unsubscribe() {
        //TODO: implement
        Timber.e("Not implemented yet.");
    }


}
