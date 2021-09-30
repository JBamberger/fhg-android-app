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

package de.jbamberger.fhgapp.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.AboutActivityBinding
import de.jbamberger.fhgapp.ui.about.license.LicenseActivity
import de.jbamberger.fhgapp.util.Utils

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.about_activity)

        binding.setContactDevListener { Utils.contactDeveloper(this) }
        binding.setVisitRepoListener { Utils.openUrl(this, R.string.repo_link) }
        binding.setVisitFhgListener { Utils.openUrl(this, R.string.fhg_link) }
        binding.setShowOssLicencesListener {
            this.startActivity(Intent(this, LicenseActivity::class.java))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.about, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> Utils.shareApplication(this)
            R.id.action_contact_developer -> Utils.contactDeveloper(this)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
