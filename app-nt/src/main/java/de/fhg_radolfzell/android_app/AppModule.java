package de.fhg_radolfzell.android_app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.util.RegistrationManager;
import de.fhg_radolfzell.android_app.util.Storage;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Bus providesEventBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    RegistrationManager providesRegistrationManager(Bus eventBus, FhgApiInterface api, Storage storage) {
        return new RegistrationManager(eventBus, api, storage);
    }

    @Provides
    @Singleton
    Context providesApplicationContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

}
