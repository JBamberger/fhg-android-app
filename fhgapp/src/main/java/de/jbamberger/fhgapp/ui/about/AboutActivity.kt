package de.jbamberger.fhgapp.ui.about

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.widget.LinearLayoutManager
import android.view.View
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

        adapter.replaceAll(addData())
    }

    fun libraryClicked(url: String) {
        Utils.openUrl(this, url)
    }

    @VisibleForTesting
    internal fun addData(): List<Item> {
        val names = resources.getStringArray(R.array.about_library_names)
        val licenses = resources.getStringArray(R.array.about_library_descriptions)
        val urls = resources.getStringArray(R.array.about_library_urls)

        if (names.size != licenses.size || names.size != urls.size) {
            throw IllegalStateException("Resources are not correctly loaded or configured.")
        }

        val items: MutableList<Item> = mutableListOf(
                Item(R.layout.about_disclaimer, null, null),
                Item(R.layout.about_contact, null,
                        View.OnClickListener { Utils.contactDeveloper(this) }),
                Item(R.layout.about_version, null, null),
                Item(R.layout.about_library_title, null, null)
        )

        for (i in names.indices) {
            items.add(Item(R.layout.about_library, Library(names[i], licenses[i], urls[i]), this))
        }

        return items
    }

    data class Library(val name: String, val description: String, val url: String)
}
