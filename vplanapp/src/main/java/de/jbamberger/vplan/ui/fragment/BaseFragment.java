package de.jbamberger.vplan.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import de.jbamberger.vplan.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {

    protected void setActionBarTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    protected void setActionBarTitle(int id) {
        getSupportActionBar().setTitle(id);
    }

    protected void setActionBarSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }

    protected void setActionBarSubtitle(int id) {
        getSupportActionBar().setSubtitle(id);
    }

    protected void showContactDevDialog() {
        ((BaseActivity) getActivity()).showContactDevDialog();
    }

    protected ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected void showSettings() {
        ((BaseActivity) getActivity()).showSettings();
    }

}
