package de.jbapps.vplan;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import de.jbamberger.jutils.MiscUtils;
import de.jbapps.vplan.util.Property;

public class SettingsActivity extends PreferenceActivity {

    final static String ACTION_PREFS_GENERAL = "de.jbapps.prefs.PREFS_GENERAL";
    final static String ACTION_PREFS_NOTIFICATION = "de.jbapps.prefs.PREFS_NOTIFICATION";
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private static Preference.OnPreferenceClickListener gradeListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(final Preference preference) {
            final List<Integer> selected = new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setTitle(R.string.text_dialog_pick_grade)
                    .setMultiChoiceItems(R.array.listGrades, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        selected.add(which);
                                    } else if (selected.contains(which)) {
                                        selected.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String[] list = preference.getContext().getResources().getStringArray(R.array.listGrades);
                            StringBuilder output = new StringBuilder();
                            for (int i : selected) {
                                output.append(list[i]);
                                output.append(",");
                            }
                            String grades = output.toString();
                            if (grades.length() > 0) {
                                grades = grades.substring(0, grades.length() - 1);
                            } else {
                                grades = "";
                            }

                            new Property(preference.getContext()).storeGrade(grades);
                        }
                    })
                    .setNegativeButton(R.string.select_all, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            new Property(preference.getContext()).storeGrade("");
                        }
                    });

            builder.create().show();
            return false;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_PREFS_GENERAL)) {
            addPreferencesFromResource(R.xml.pref_general);
            findPreference(getString(R.string.pref_key_grades)).setOnPreferenceClickListener(gradeListener);
            findPreference(getString(R.string.pref_key_version)).setSummary(MiscUtils.getVersionName(this));
        } else if (action != null && action.equals(ACTION_PREFS_NOTIFICATION)) {
            addPreferencesFromResource(R.xml.pref_notification);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification_ringtone)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification_light)));
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.pref_header_legacy);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferenceFragment.class.getName().equals(fragmentName) || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            findPreference(getString(R.string.pref_key_grades)).setOnPreferenceClickListener(gradeListener);
            findPreference(getString(R.string.pref_key_version)).setSummary(MiscUtils.getVersionName(getActivity()));

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification_ringtone)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification_light)));
        }
    }
}
