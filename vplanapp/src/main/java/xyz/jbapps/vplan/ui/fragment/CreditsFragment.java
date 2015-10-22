package xyz.jbapps.vplan.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;

public class CreditsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setActionBarSubtitle(R.string.app_copyright);
        setActionBarTitle(R.string.title_fragment_credits);
        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((WebView) view.findViewById(R.id.webView)).loadUrl("file:///android_asset/vplan_licence.txt");
        ViewUtils.findViewById(view, R.id.action_contact_developer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactDevDialog();
            }
        });
    }
}
