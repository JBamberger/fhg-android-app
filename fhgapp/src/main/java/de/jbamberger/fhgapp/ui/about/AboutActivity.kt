package de.jbamberger.fhgapp.ui.about

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.ui.components.DataBindingAdapter
import de.jbamberger.fhgapp.ui.components.DataBindingAdapter.Item
import de.jbamberger.fhgapp.util.Utils
import kotlinx.android.synthetic.main.about_activity.*

class AboutActivity : BaseActivity() {

    private val adapter: DataBindingAdapter = DataBindingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { Utils.shareApplication(this) }
        aboutContainer.adapter = adapter
        aboutContainer.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addData()
    }

    fun libraryClicked(url: String) {
        Utils.openUrl(this, url)
    }

    private fun addData() {
        val items: List<Item> = listOf(
                Item(R.layout.about_disclaimer, null, null),
                Item(R.layout.about_contact, null,
                        View.OnClickListener { Utils.contactDeveloper(this) })
                //Item(R.layout.about_version, null, null)
                //TODO: add all libs
        )
        adapter.replaceAll(items)
    }

    data class Library(val name: String, val description: String, val url: String)
}
