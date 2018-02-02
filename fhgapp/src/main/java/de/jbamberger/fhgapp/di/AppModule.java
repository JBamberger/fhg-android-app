package de.jbamberger.fhgapp.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.jbamberger.api.FhgApi;
import de.jbamberger.fhgapp.source.db.AppDatabase;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module(includes = {ViewModelModule.class})
class AppModule {

    @Provides
    @Singleton
    Context providesApplicationContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    FhgApi providesFhgApi(Application application) {
        return new FhgApi.Provider(application).api;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    Gson providesGson(){
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "fhg-db.sglite").build();
    }
}
