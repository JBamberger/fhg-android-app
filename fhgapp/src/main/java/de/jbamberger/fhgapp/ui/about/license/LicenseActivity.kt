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

    private class DependencyAdapter(val items: List<OssDependencyListItem>) :
        DataBindingBaseAdapter() {
        override fun getObjForPosition(position: Int) = items[position]
        override fun getLayoutIdForPosition(position: Int) = items[position].layoutId
        override fun getItemCount() = items.size
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

        viewModel.dependencies.observe(this) { items ->
            if (items != null) {
                binding.licenseList.adapter = DependencyAdapter(items)
            }
        }
    }

//    fun handleItemClick(item: DependencyInformation) {
//
//    }


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
