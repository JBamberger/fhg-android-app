package de.fhg_radolfzell.android_app.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.FHGApplication;
import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.databinding.ActivityMainBinding;
import de.fhg_radolfzell.android_app.databinding.DrawerHeaderBinding;
import de.fhg_radolfzell.android_app.event.UpdateMainSubTitleEvent;
import de.fhg_radolfzell.android_app.main.calendar.CalendarFragment;
import de.fhg_radolfzell.android_app.main.credits.CreditsFragment;
import de.fhg_radolfzell.android_app.main.feed.FeedFragment;
import de.fhg_radolfzell.android_app.main.settings.SettingsFragment;
import de.fhg_radolfzell.android_app.main.vplan.VPlanFragment;
import de.fhg_radolfzell.android_app.util.Storage;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private static final String TAG = "MainActivity";
    private static final String SELECTED_FRAGMENT = "selected_fragment";

    @Inject
    Storage storage;
    @Inject
    Bus eventBus;

    private MainModule mainModule;
    private MainComponent mainComponent;
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    private boolean doubleBackToExitPressedOnce = false;
    @IdRes
    private int selectedFragment;
    private int fragmentTitle = R.string.app_name;
    private String fragmentSubTitle = null;

    public MainComponent getMainComponent() {
        return mainComponent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!outState.containsKey(SELECTED_FRAGMENT)) {
            outState.putInt(SELECTED_FRAGMENT, selectedFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainModule = new MainModule(this);
        mainComponent = ((FHGApplication) getApplication()).getAppComponent().newMainComponent(mainModule);
        mainComponent.inject(this);
        super.onCreate(savedInstanceState);

        //create bindings
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DrawerHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer_header, binding.navigationView, false);

        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.accessibility_navigation_open, R.string.accessibility_navigation_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                binding.toolbar.setTitle(fragmentTitle);
                binding.toolbar.setSubtitle(fragmentSubTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                binding.toolbar.setTitle(R.string.app_name);
                binding.toolbar.setSubtitle(null);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.addHeaderView(headerBinding.getRoot());

        headerBinding.contactToolbar.inflateMenu(R.menu.menu_drawer_contact);
        headerBinding.setListener(this);
        applyToolbarItemColor(headerBinding.contactToolbar.getMenu(), R.color.toolbar_textColorPrimary);
        binding.toolbar.setTitle(fragmentTitle); // dummy title, so the title of the Fragment isn't overwritten.
        setSupportActionBar(binding.toolbar);

        if (storage.getShowInitialSettings()) {
            selectedFragment = R.id.drawer_settings;
            storage.setShowInitialSettings(false);
        } else {
            // Apply correct fragment
            if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_FRAGMENT)) {
                selectedFragment = savedInstanceState.getInt(SELECTED_FRAGMENT);
            } else {
                selectedFragment = storage.getLastSelectedFragment();
            }
        }


        applySelectedFragment(selectedFragment == 0 ? R.id.drawer_vplan : selectedFragment);
    }

    @Override
    protected void onResume() {
        eventBus.register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            eventBus.unregister(this);
        } catch (Exception e) {
            Timber.e("onPause: bus unregistration failed");
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        storage.setLastSelectedFragment(selectedFragment);
    }

    public boolean onHeaderItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_drawer_contact_phone:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.text_fhg_contact_phone))));
                return true;
            case R.id.action_drawer_contact_email:
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.text_fhg_contact_mail))));
                return true;
            case R.id.action_drawer_contact_navigate:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=Friedrich-Hecker-Gymnasium,+Markelfinger+Straße,+Radolfzell+am+Bodensee"); //TODO: remove hrdcoded string
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            case R.id.action_drawer_contact_full:
                new AlertDialog.Builder(this)
                        .setView(R.layout.contact_dialog)
//                        .setTitle(R.string.text_fhg_name)
//                        .setMessage(R.string.text_fhg_contact)
                        //.setIcon(headerImage)
                        .setCancelable(true)
                        .create()
                        .show();
                return true;
        }
        return false;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //item.setChecked(true);
        boolean success = applySelectedFragment(item.getItemId());
        binding.drawerLayout.closeDrawer(binding.navigationView);
//        Toast.makeText(getApplicationContext(), "Item clicked: " + item.getItemId(), Toast.LENGTH_SHORT).show();
        return success;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                    binding.drawerLayout.closeDrawer(binding.navigationView);
                } else {
                    binding.drawerLayout.openDrawer(binding.navigationView);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.action_back_pressed, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); //2 sec.
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (binding.drawerLayout != null) {
            boolean drawerOpen = binding.drawerLayout.isDrawerOpen(binding.navigationView);
            // TODO: 22.03.2017 hide appropriate options
            //menu.findItem(R.id.action_contact_developer).setVisible(!drawerOpen);
            //menu.findItem(R.id.action_calendar_export_ics).setVisible(!drawerOpen);
            //menu.findItem(R.id.action_calendar_export_xml).setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_contact_developer) {
            showContactDevDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void applyToolbarItemColor(Menu menu, @ColorRes int colorId) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(colorId), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private boolean applySelectedFragment(@IdRes int fragment) {

        switch (fragment) {
            case R.id.drawer_vplan:
                fragmentTitle = R.string.title_fragment_vplan;
                VPlanFragment frag = new VPlanFragment();
                frag.setMainView(this);
                applyFragment(frag);
                break;
            case R.id.drawer_fhg_feed:
                fragmentTitle = R.string.title_fragment_fhg_feed;
                applyFragment(new FeedFragment());
                break;
            case R.id.drawer_credits:
                fragmentTitle = R.string.title_fragment_credits;
                applyFragment(new CreditsFragment());
                break;
            case R.id.drawer_calendar:
                fragmentTitle = R.string.title_fragment_calendar;
                applyFragment(new CalendarFragment());
                break;
            case R.id.drawer_settings:
                fragmentTitle = R.string.title_fragment_settings;
                applyFragment(new SettingsFragment());
                break;
            default:
                return false;
        }

        if (binding.toolbar != null) {
            fragmentSubTitle = null;
            binding.toolbar.setTitle(fragmentTitle);
            binding.toolbar.setSubtitle(fragmentSubTitle);
        }
        binding.navigationView.setCheckedItem(fragment);
        selectedFragment = fragment;
        return true;
    }

    @Subscribe
    public void updateSubTitle(UpdateMainSubTitleEvent subtitle) {
        if (binding.toolbar != null) {
            fragmentSubTitle = subtitle.subtitle;
            binding.toolbar.setSubtitle(fragmentSubTitle);
        }
    }

    private void applyFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    public void showContactDevDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_dialog_contact_developer)
                .setItems(R.array.mail_subjects, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        if (0 > pos || pos > 3)
                            pos = 3;
                        String subject = getResources().getStringArray(R.array.mail_subjects)[pos];
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.mail_developer)));
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    public MainModule getMainModule() {
        return mainModule;
    }

    @Override
    public void setSubtitle(String subtitle) {
        if (binding.toolbar != null) {
            fragmentSubTitle = subtitle;
            binding.toolbar.setSubtitle(fragmentSubTitle);
        }
    }

    @Override
    public void setSubtitle(@StringRes int subtitle) {
        if (binding.toolbar != null) {
            fragmentSubTitle = getString(subtitle);
            binding.toolbar.setSubtitle(fragmentSubTitle);
        }
    }

    @Override
    public void clearSubtitle() {
        if (binding.toolbar != null) {
            fragmentSubTitle = null;
            binding.toolbar.setSubtitle(null);
        }
    }
}
