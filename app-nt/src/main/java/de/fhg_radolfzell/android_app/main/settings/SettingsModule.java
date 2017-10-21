package de.fhg_radolfzell.android_app.main.settings;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.util.RegistrationManager;
import de.fhg_radolfzell.android_app.util.Storage;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@Module
public class SettingsModule {

    @Provides
    @SettingsScope
    SettingsInteractor getSettingsInteractor(Context context, Storage storage, RegistrationManager registrationManager) {
        return new SettingsInteractorImpl(storage, registrationManager, context);
    }
}
