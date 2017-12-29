package de.jbamberger.fhgapp.ui

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ActivityMainBinding
import de.jbamberger.fhgapp.ui.components.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class MainActivity : BaseActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_vplan -> {
                viewModel!!.selectedVPlan()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_feed -> {
                viewModel!!.selectedFeed()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contact -> {
                viewModel!!.selectedContact()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @Inject
    @JvmField
    var viewModel: MainViewModel? = null
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewModel!!.init()
        viewModel!!.getFragment().observe(this, Observer { frag -> showFragment(frag) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_share -> return true
            R.id.action_settings -> return true
            R.id.action_about -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(frag: Fragment?) {
        val fragment: Fragment = frag ?: Fragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
    }
}
