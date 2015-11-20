package xyz.jbapps.vplan.ui.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.jbapps.vplan.R;

/**
 * A placeholder fragment containing a simple view.
 *
 * @author autogen
 * @version 1.0
 */
public class BaseActivityFragment extends Fragment {

    public BaseActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }
}
