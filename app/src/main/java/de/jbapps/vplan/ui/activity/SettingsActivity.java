package de.jbapps.vplan.ui.activity;

import android.preference.PreferenceActivity;

import java.util.List;

import de.jbapps.vplan.R;
import de.jbapps.vplan.ui.fragment.NotificationFragment;
import de.jbapps.vplan.ui.fragment.ViewFragment;

public class SettingsActivity extends PreferenceActivity {

    protected boolean isValidFragment(String fragmentName) {
        return ViewFragment.class.getName().equals(fragmentName) || NotificationFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
}
