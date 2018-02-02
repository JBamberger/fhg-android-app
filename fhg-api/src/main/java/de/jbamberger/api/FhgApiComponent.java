package de.jbamberger.api;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Singleton
@Component(modules = {
        FhgApiModule.class,
        NetModule.class
})
public interface FhgApiComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        FhgApiComponent build();
    }

    void inject(FhgApi.Provider builder);
}
