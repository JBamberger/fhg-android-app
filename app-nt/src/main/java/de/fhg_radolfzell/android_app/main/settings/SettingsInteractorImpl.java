package de.fhg_radolfzell.android_app.main.settings;

import android.content.Context;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.util.RegistrationManager;
import de.fhg_radolfzell.android_app.util.Storage;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

@SettingsScope
public class SettingsInteractorImpl implements SettingsInteractor {

    private Context context;
    private Storage storage;
    private RegistrationManager registrationManager;
    private Set<String> changedPrefs;

    @Inject
    public SettingsInteractorImpl(Storage storage, RegistrationManager registrationManager, Context context) {
        this.context = context;
        this.storage = storage;
        this.registrationManager = registrationManager;
        this.changedPrefs = new TreeSet<>();
    }

    @Override
    public void submitChanges() {
        storage.setFcmSubscribed(false);
        if (changedPrefs.contains(context.getString(R.string.pref_grade_show_all_key))
                || changedPrefs.contains(context.getString(R.string.pref_grade_key))) {
            registrationManager.subscribe();
        }

        changedPrefs.clear();
    }

    @Override
    public void addChangedPreference(String preference) {
        changedPrefs.add(preference);
    }
}
