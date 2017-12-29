package de.jbamberger.fhgapp.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ActivityMainBinding
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.vplan.VPlanFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_vplan -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, VPlanFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_feed -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, Fragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contact -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, Fragment())
                        .commit()
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.action_share -> return true
            R.id.action_settings -> return true
            R.id.action_about -> return true
        }

        return super.onOptionsItemSelected(item)
    }
}
