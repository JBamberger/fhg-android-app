package de.jbamberger.fhgapp.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.jbamberger.api.FhgApiModule;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module(includes = {ViewModelModule.class, FhgApiModule.class})
class AppModule {

    @Provides
    @Singleton
    Context providesApplicationContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
