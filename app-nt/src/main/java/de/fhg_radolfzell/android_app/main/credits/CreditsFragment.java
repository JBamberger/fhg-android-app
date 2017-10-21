package de.fhg_radolfzell.android_app.main.credits;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.FHGApplication;
import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.databinding.FragmentCreditsBinding;
import de.fhg_radolfzell.android_app.event.UpdateMainSubTitleEvent;

public class CreditsFragment extends Fragment {

    private static final String TAG = "CreditsFragment";

    @Inject
    Context context;

    @Inject
    Bus eventBus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FHGApplication) getActivity().getApplication()).getAppComponent().inject(this);
        eventBus.post(new UpdateMainSubTitleEvent(context.getString(R.string.credits_copyright)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCreditsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_credits, container, false);
        binding.webView.loadUrl("file:///android_asset/open_source_licenses.html");
        return binding.getRoot();
    }
}
