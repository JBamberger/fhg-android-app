package de.jbamberger.api;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module
public abstract class FhgApiModule {

    @Binds
    @Singleton
    public abstract FhgApi bindsFhgApi(FhgApiImpl impl);
}
