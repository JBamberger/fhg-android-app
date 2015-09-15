package xyz.jbapps.vplan.ui.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.util.Property;

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Property p = new Property(getActivity());
        p.setShowSettings(false);
        addPreferencesFromResource(R.xml.preference_vplan);
    }
}
