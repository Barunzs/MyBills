package bill.com.mybills.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import bill.com.mybills.R
import kotlinx.android.synthetic.main.activity_webview.*





class WebviewActivity : AppCompatActivity() {

	private var webView: WebView? = null


	var activity: Activity? = null
	private var progDailog: ProgressDialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_webview)
		val url = intent.getStringExtra("URL")

		activity = this







		pdfView.settings.javaScriptEnabled = true
		pdfView.settings.loadWithOverviewMode = true
		pdfView.settings.useWideViewPort = true
		pdfView.webViewClient = object : WebViewClient() {

			override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

				view.loadUrl(url)

				return true
			}

			override fun onPageFinished(view: WebView, url: String) {

			}
		}

		pdfView.loadUrl(url)
	}
}