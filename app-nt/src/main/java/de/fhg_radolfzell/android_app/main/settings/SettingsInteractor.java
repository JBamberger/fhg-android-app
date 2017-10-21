package de.fhg_radolfzell.android_app.main.settings;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface SettingsInteractor {
    void submitChanges();
    void addChangedPreference(String preference);

}
