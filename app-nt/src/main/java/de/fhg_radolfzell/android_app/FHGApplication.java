package de.fhg_radolfzell.android_app;

import android.app.Application;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.util.RegistrationManager;
import de.fhg_radolfzell.android_app.util.Storage;
import timber.log.Timber;

public class FHGApplication extends Application {

    private static final String TAG = "FHGApplication";

    private AppComponent appComponent;

    @Inject
    Bus eventBus;
    @Inject
    Storage storage;
    @Inject
    RegistrationManager registrationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        appComponent.inject(this);
        eventBus.register(DeadEvent.class);
        eventBus.register(storage);

        //restart registration if failed previously
        if(!registrationManager.isSubscribed()) {
            registrationManager.subscribe();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        eventBus.unregister(storage);
    }

    /**
     * probably useless
     */
    @Subscribe
    void handleDeadEvents(DeadEvent deadEvent) {
        //TODO do something instead of ignoring
        Timber.e("handleDeadEvents: %s", deadEvent.toString());
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }


    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            //FIXME: use reporting library
            Log.e(tag, message, t);
            /*FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }*/
        }
    }
}