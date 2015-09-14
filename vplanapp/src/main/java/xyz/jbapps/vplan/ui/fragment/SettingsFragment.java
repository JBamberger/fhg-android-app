package xyz.jbapps.vplan.ui.fragment;


import android.os.Bundle;
import xyz.jbapps.vplan.support.v4.preference.PreferenceFragment;

import xyz.jbapps.vplan.R;

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_settings);
        addPreferencesFromResource(R.xml.preference_vplan);
    }
}
