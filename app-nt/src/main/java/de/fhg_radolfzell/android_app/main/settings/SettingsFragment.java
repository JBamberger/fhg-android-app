package de.fhg_radolfzell.android_app.main.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.Set;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.main.MainActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Inject
    SharedPreferences preferences;

    @Inject
    SettingsInteractor interactor;

    SettingsComponent settingsComponent;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_vplan, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsComponent = ((MainActivity) getActivity()).getMainComponent().newSettingsComponent(new SettingsModule());
        settingsComponent.inject(this);

        addPreferencesFromResource(R.xml.preference_vplan);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        interactor.submitChanges();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_grade_show_all_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_grade_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_course_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_enable_analytics_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notification_enabled_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_enable_crash_reporting_key)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (value == null) {
            return false;
        }

        String stringValue = value.toString();
        String key = preference.getKey();
        interactor.addChangedPreference(key);

        if (key.equals(getString(R.string.pref_grade_key))) { // grades changed
            if (value instanceof Set && ((Set) value).isEmpty()) {
                preference.setSummary(getString(R.string.pref_grade_summary));
            } else {
                preference.setSummary(stringValue);
            }
        } else if (key.equals(getString(R.string.pref_course_key))) { // courses changed
            preference.setSummary(stringValue.isEmpty() ? getString(R.string.pref_course_summary) : stringValue);
        }

        return true;

    }

    /**
     * Used to update the text of Preference items
     *
     * @param preference updated item
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.

        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_grade_key))) {
            onPreferenceChange(preference, preferences.getStringSet(preference.getKey(), null));
        }
        if (key.equals(getString(R.string.pref_course_key))) {
            onPreferenceChange(preference, preferences.getString(preference.getKey(), null));
        }
    }
}
