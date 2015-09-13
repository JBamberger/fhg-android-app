package xyz.jbapps.vplan.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

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

    protected ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
