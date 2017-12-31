package de.jbamberger.fhgapp.ui.about

import android.os.Bundle
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.ui.components.BaseActivity
import de.jbamberger.fhgapp.util.Utils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { Utils.shareApplication(this) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
