package de.jbamberger.fhgapp.ui.about

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
        setSupportActionBar(toolbar)
        about_action_share.setOnClickListener { Utils.shareApplication(this) }
        aboutContainer.adapter = adapter
        aboutContainer.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter.replaceAll(listOf(
                Item(R.layout.about_disclaimer, null, null),
                Item(R.layout.about_contact, null,
                        View.OnClickListener { Utils.contactDeveloper(this) }),
                Item(R.layout.about_version, null, null),
                Item(R.layout.about_oss_licenses, null,
                        View.OnClickListener { startActivity(Intent(this, OssLicensesMenuActivity::class.java)) })
                ))
    }

    fun libraryClicked(url: String) {
        Utils.openUrl(this, url)
    }

    data class Library(val name: String, val description: String, val url: String)
}
