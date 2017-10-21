package de.fhg_radolfzell.android_app.main.settings;

import dagger.Subcomponent;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@SettingsScope
@Subcomponent(
        modules = {
                SettingsModule.class
        }
)
public interface SettingsComponent {
    void inject(SettingsFragment settingsFragment);
    void inject(SettingsInteractor interactor);
}
