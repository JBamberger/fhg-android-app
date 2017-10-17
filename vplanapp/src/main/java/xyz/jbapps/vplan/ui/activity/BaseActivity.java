package xyz.jbapps.vplan.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.fragment.ContactFragment;
import xyz.jbapps.vplan.ui.fragment.CreditsFragment;
import xyz.jbapps.vplan.ui.fragment.FHGFeedFragment;
import xyz.jbapps.vplan.ui.fragment.VPlanFragment;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private static final String SELECTED_FRAGMENT = "selected_fragment";

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private int selectedFragment = R.id.drawer_vplan;
    private VPlanFragment vPlanFragment = null;
    private FHGFeedFragment fhgFeedFragment = null;
    private ContactFragment contactFragment = null;
    private CreditsFragment creditsFragment = null;

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

        Log.d(TAG, "Created Activity");

        setContentView(R.layout.activity_base);
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
            showContactDevDialog();
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
                if(menuItem.getItemId() == R.id.drawer_settings) {
                    showSettings();
                    navigationView.setCheckedItem(selectedFragment);
                    drawerLayout.closeDrawer(navigationView);
                    return true;
                } else {
                    selectedFragment = menuItem.getItemId();
                    boolean applied = applySelectedFragment();
                    drawerLayout.closeDrawer(navigationView);
                    return applied;
                }

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
                if (fhgFeedFragment == null) {
                    fhgFeedFragment = new FHGFeedFragment();
                }
                applyFragment(fhgFeedFragment);
                break;
            case R.id.drawer_contact:
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                }
                applyFragment(contactFragment);
                break;
            case R.id.drawer_credits:
                if (creditsFragment == null) {
                    creditsFragment = new CreditsFragment();
                }
                applyFragment(creditsFragment);
                break;
            case R.id.drawer_settings:
                showSettings();
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
