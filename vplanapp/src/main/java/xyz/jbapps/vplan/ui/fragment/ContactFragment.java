package xyz.jbapps.vplan.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;

public class ContactFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_contact);

        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        ViewUtils.findViewById(v, R.id.button_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.text_fhg_contact_mail))));
            }
        });

        ViewUtils.findViewById(v, R.id.button_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.text_fhg_contact_phone))));
            }
        });
        return v;
    }
}
