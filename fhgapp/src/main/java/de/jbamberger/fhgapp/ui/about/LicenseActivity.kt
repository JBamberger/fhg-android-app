package de.jbamberger.fhgapp.ui.about

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class LicenseActivity : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wv = WebView(this)
        wv.webViewClient = WebViewClient()
        wv.loadUrl("file:///android_asset/licenses.html")
        webView = wv
        setContentView(webView)
    }

    override fun onBackPressed() {
        webView?.let {
            if (it.copyBackForwardList().currentIndex > 0) {
                it.goBack()
                return
            }
        }

        super.onBackPressed()
    }
}
