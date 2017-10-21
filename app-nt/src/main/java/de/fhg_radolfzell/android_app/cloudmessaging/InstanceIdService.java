package de.fhg_radolfzell.android_app.cloudmessaging;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.FHGApplication;
import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.util.RegistrationManager;
import de.fhg_radolfzell.android_app.util.Storage;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 04.08.2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIdService";
    @Inject
    FhgApiInterface api;
    @Inject
    Storage storage;
    @Inject
    RegistrationManager registrationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ((FHGApplication) getApplication()).getAppComponent().inject(this);
    }

    /**
     * Called if InstanceID token is updated.
     */
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("Refreshed token: %s", refreshedToken);

        storage.setFcmToken(refreshedToken);
        storage.setFcmSubscribed(false);

        registrationManager.subscribe();
    }
}
