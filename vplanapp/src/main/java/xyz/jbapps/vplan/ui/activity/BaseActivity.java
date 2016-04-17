package xyz.jbapps.vplan.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.fragment.ContactFragment;
import xyz.jbapps.vplan.ui.fragment.CreditsFragment;
import xyz.jbapps.vplan.ui.fragment.FeedFragment;
import xyz.jbapps.vplan.ui.fragment.PostFragment;
import xyz.jbapps.vplan.ui.fragment.SettingsFragment;
import xyz.jbapps.vplan.ui.fragment.VPlanFragment;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private static final String SELECTED_FRAGMENT = "selected_fragment";

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Context context;
    private int selectedFragment = R.id.drawer_vplan;
    private VPlanFragment vPlanFragment = null;
    private FeedFragment feedFragment = null;
    private PostFragment postFragment = null;
    private ContactFragment contactFragment = null;
    private CreditsFragment creditsFragment = null;
    private SettingsFragment settingsFragment = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        context = this;
        setupUI();
        if (savedInstanceState != null) {
            selectedFragment = savedInstanceState.getInt(SELECTED_FRAGMENT, R.id.drawer_vplan);
        }
        applySelectedFragment();
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
            startActivity(new Intent(this, ExperimentsActivity.class));
            //showContactDevDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showContactDevDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_dialog_contact_developer)
                .setItems(R.array.mail_subjects, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String subject;
                        switch (which) {
                            case 0:
                                subject = getString(R.string.text_subject_question);
                                break;
                            case 1:
                                subject = getString(R.string.text_subject_feedback);
                                break;
                            case 2:
                                subject = getString(R.string.text_subject_bug);
                                break;
                            default:
                                subject = getString(R.string.text_subject_general);
                                break;
                        }
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.mail_developer)));
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    private void showContactFHGDialog() {
        Drawable headerImage = getResources().getDrawable(R.drawable.header_logo);
        if (headerImage != null) {
            headerImage.mutate();
            headerImage.setColorFilter(getResources().getColor(R.color.toolbar_textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        new AlertDialog.Builder(this).setTitle(R.string.text_fhg_name)
                .setMessage(R.string.text_fhg_contact)
                .setIcon(headerImage)
                .setCancelable(true)
                .create()
                .show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_FRAGMENT, selectedFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void setupUI() {
        Toolbar mToolbar = ViewUtils.findViewById(this, R.id.toolbar);
        drawerLayout = ViewUtils.findViewById(this, R.id.drawerLayout);
        navigationView = ViewUtils.findViewById(this, R.id.navigationView);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.ok, R.string.cancel);
        drawerLayout.setDrawerListener(drawerToggle);

        setSupportActionBar(mToolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectedFragment = menuItem.getItemId();
                boolean applied = applySelectedFragment();
                drawerLayout.closeDrawer(navigationView);
                return applied;

            }
        });
        ImageView imageView = ViewUtils.findViewById(navigationView.getHeaderView(0), R.id.header_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactFHGDialog();
            }
        });

        Toolbar contactToolbar = ViewUtils.findViewById(navigationView.getHeaderView(0), R.id.contact_toolbar);
        contactToolbar.inflateMenu(R.menu.menu_drawer_contact);
        Menu menu = contactToolbar.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.toolbar_textColorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        contactToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_drawer_contact_phone:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.text_fhg_contact_phone))));
                        return true;
                    case R.id.action_drawer_contact_email:
                        startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.text_fhg_contact_mail))));
                        return true;
                    case R.id.action_drawer_contact_navigate:
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=Friedrich-Hecker-Gymnasium,+Markelfinger+Straße,+Radolfzell+am+Bodensee");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        return true;
                    case R.id.action_drawer_contact_full:
                        showContactFHGDialog();
                        return true;
                }
                return false;
            }
        });
    }


    private boolean applySelectedFragment() {
        switch (selectedFragment) {
            case R.id.drawer_vplan:
                if (vPlanFragment == null) {
                    vPlanFragment = new VPlanFragment();
                }
                applyFragment(vPlanFragment);
                break;
            case R.id.drawer_fhg_feed:
                /*if (feedFragment == null) {
                    feedFragment = new FeedFragment();
                }
                applyFragment(feedFragment);*/
                if (postFragment == null) {
                    postFragment = new PostFragment();
                }
                applyFragment(postFragment);
                break;
            /*case R.id.drawer_contact:
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                }
                applyFragment(contactFragment);
                break;*/
            case R.id.drawer_credits:
                if (creditsFragment == null) {
                    creditsFragment = new CreditsFragment();
                }
                applyFragment(creditsFragment);
                break;
            case R.id.drawer_settings:
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.title_fragment_settings);
                    actionBar.setSubtitle("");
                }
                applyFragment(settingsFragment);
                break;
            default:
                return false;
        }
        return true;
    }

    public void showSettings() {
        navigationView.setCheckedItem(selectedFragment);
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void applyFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
