package de.jbamberger.fhgapp.ui.about

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.AboutActivityBinding
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.components.DataBindingAdapter
import de.jbamberger.fhgapp.util.Utils

class AboutActivity : BaseActivity<AboutViewModel>() {

    private val adapter: DataBindingAdapter = DataBindingAdapter()

    override val viewModelClass: Class<AboutViewModel>
        get() = AboutViewModel::class.java

    private lateinit var binding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.about_activity)

        binding.setContactDevListener { Utils.contactDeveloper(this) }
        binding.setVisitRepoListener { Utils.openUrl(this, R.string.repo_link) }
        binding.setShowOssLicencesListener { startActivity(Intent(this, OssLicensesMenuActivity::class.java)) }
        binding.setVisitFhgListener { Utils.openUrl(this, R.string.fhg_link) }

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
