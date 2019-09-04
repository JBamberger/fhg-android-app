package de.jbamberger.fhgapp.ui.about

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.components.DataBindingAdapter
import de.jbamberger.fhgapp.ui.components.DataBindingAdapter.Item
import de.jbamberger.fhgapp.util.Utils
import kotlinx.android.synthetic.main.about_activity.*

class AboutActivity : BaseActivity<AboutViewModel>() {

    private val adapter: DataBindingAdapter = DataBindingAdapter()

    override val viewModelClass: Class<AboutViewModel>
        get() = AboutViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)

        aboutContainer.adapter = adapter
        aboutContainer.layoutManager = LinearLayoutManager(this)
        aboutContainer.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter.replaceAll(listOf(
                Item(R.layout.about_disclaimer, null, null),
                Item(R.layout.about_contact, null,
                        View.OnClickListener { Utils.contactDeveloper(this) }),
                Item(R.layout.about_version, null, null),
                Item(R.layout.about_oss_licenses, null,
                        View.OnClickListener {
                            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                        })
        ))
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
