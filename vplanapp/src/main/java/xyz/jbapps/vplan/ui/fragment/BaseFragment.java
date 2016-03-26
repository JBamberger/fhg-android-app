package xyz.jbapps.vplan.ui.fragment;


import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import xyz.jbapps.vplan.ui.activity.BaseActivity;
/**
 * Fragment providing basic accessors to modify the host Activity's view
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
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
