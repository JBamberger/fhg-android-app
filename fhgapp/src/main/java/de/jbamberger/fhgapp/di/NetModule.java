package de.jbamberger.fhgapp.di;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
class NetModule {

    @Provides
    @Singleton
    de.jbamberger.api.FhgApi providesFhgApi(@NonNull Context context) {
        return de.jbamberger.api.FhgApi.Builder.getInstance(context);
    }
}
