package de.jbamberger.fhgapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ActivityMainBinding
import de.jbamberger.fhgapp.ui.about.AboutActivity
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.settings.SettingsActivity
import de.jbamberger.fhgapp.util.Utils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class MainActivity : BaseActivity() {

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

    lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(navigationListener)

        viewModel.getFragment().observe(this, Observer { frag -> showFragment(frag) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_share -> {
                Utils.shareApplication(this)
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.action_contact_developer -> {
                Utils.contactDeveloper(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
