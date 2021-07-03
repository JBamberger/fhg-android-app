package de.jbamberger.fhgapp.ui.about.license

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.databinding.LicenseActivityBinding
import de.jbamberger.fhgapp.ui.DataBindingBaseAdapter


@AndroidEntryPoint
class LicenseActivity : AppCompatActivity() {

    private val viewModel: LicenseViewModel by viewModels()

    private class DependencyAdapter(val deps: List<DependencyItem>) : DataBindingBaseAdapter() {
        override fun getObjForPosition(position: Int) = deps[position]
        override fun getLayoutIdForPosition(position: Int) = R.layout.license_list_item
        override fun getItemCount() = deps.size
    }

    private lateinit var binding: LicenseActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.license_activity)

        val layoutManager = LinearLayoutManager(this)
        binding.licenseList.layoutManager = layoutManager
        binding.licenseList.addItemDecoration(
            DividerItemDecoration(this, layoutManager.orientation)
        )

        viewModel.dependencies.observe(this) { depInfoList ->
            if (depInfoList != null) {
                val items = depInfoList.map { dep ->
                    val name = dep.groupId + ":" + dep.artifactId + ":" + dep.version

                    val license = StringBuilder()

                    for (lo in dep.knownLicenses) {
                        if (license.isNotEmpty()) {
                            license.append("\n")
                        }
                        license.append(lo.name)
                    }
                    for (lo in dep.unknownLicenses) {
                        if (license.isNotEmpty()) {
                            license.append("\n")
                        }
                        if (lo.name.isNullOrBlank()) {
                            license.append(lo.url)
                        } else {
                            license.append(lo.name)
                        }
                    }
                    DependencyItem(name, license.toString(), null)
                }
                binding.licenseList.adapter = DependencyAdapter(items)
            }
        }


//        private var webView: WebView? = null
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            val wv = WebView(this)
//            wv.webViewClient = WebViewClient()
//            wv.loadUrl("file:///android_asset/licenses.html")
//            webView = wv
//            setContentView(webView)
//        }
//
//        override fun onBackPressed() {
//            webView?.let {
//                if (it.copyBackForwardList().currentIndex > 0) {
//                    it.goBack()
//                    return
//        }
    }
}
