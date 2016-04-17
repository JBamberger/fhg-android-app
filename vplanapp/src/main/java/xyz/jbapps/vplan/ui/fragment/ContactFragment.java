package xyz.jbapps.vplan.ui.fragment;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;

public class ContactFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setActionBarSubtitle("");
        setActionBarTitle(R.string.title_fragment_contact);

        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.toolbar_textColorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_contact_mail:
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.text_fhg_contact_mail))));
                break;
            case R.id.action_contact_phone:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.text_fhg_contact_phone))));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
