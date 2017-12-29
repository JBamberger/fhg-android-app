package de.jbamberger.fhgapp.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import de.jbamberger.fhgapp.R;
import de.jbamberger.fhgapp.databinding.ActivityMainBinding;
import de.jbamberger.fhgapp.ui.components.BaseActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Inject
    MainViewModel viewModel;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.mainContent.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.mainContent.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.mainContent.fab.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, Main2Activity.class)));
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
