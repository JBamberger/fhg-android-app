package de.jbamberger.fhgapp.ui

import androidx.lifecycle.Observer
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ActivityMainBinding
import de.jbamberger.fhgapp.ui.about.AboutActivity
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.settings.SettingsActivity
import de.jbamberger.fhgapp.util.Utils

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class MainActivity : BaseActivity<MainViewModel>() {

    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

    private val navigationListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_vplan -> viewModel.selectedVPlan()
                    R.id.navigation_feed -> viewModel.selectedFeed()
                    R.id.navigation_contact -> viewModel.selectedContact()
                    else -> return@OnNavigationItemSelectedListener false
                }
                return@OnNavigationItemSelectedListener true
            }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.navigation.setOnNavigationItemSelectedListener(navigationListener)

        viewModel.getFragment().observe(this, this::showFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> Utils.shareApplication(this)
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.action_contact_developer -> Utils.contactDeveloper(this)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showFragment(frag: Fragment?) {
        supportActionBar?.subtitle = null
        val fragment: Fragment = frag ?: Fragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
    }

    fun setSubtitle(subtitle: String) {
        supportActionBar?.subtitle = subtitle
    }
}
