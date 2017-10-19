package de.jbamberger.vplan.ui.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.jbamberger.vplan.R;
import de.jbamberger.vplan.util.Property;

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Property p = new Property(getActivity());
        p.setShowSettings(false);
        addPreferencesFromResource(R.xml.preference_vplan);
    }
}
