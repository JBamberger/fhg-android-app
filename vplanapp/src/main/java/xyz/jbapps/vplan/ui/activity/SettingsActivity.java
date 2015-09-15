package xyz.jbapps.vplan.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    private SettingsFragment settingsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupUI();
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, settingsFragment)
                .commit();
    }

    private void setupUI() {
        Toolbar mToolbar = ViewUtils.findViewById(this, R.id.toolbar);
        mToolbar.setSubtitle("");
        mToolbar.setTitle(R.string.title_fragment_settings);

        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
