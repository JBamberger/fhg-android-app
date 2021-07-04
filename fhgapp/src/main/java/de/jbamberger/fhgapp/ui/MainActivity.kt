/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.fhgapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.ActivityMainBinding
import de.jbamberger.fhgapp.ui.about.AboutActivity
import de.jbamberger.fhgapp.ui.settings.SettingsActivity
import de.jbamberger.fhgapp.util.Utils

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

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
