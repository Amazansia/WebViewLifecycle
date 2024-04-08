package com.example.webviewlifecycle

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import com.example.webviewlifecycle.databinding.MainActivityBinding

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var binding: MainActivityBinding
    private val viewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()

        binding = MainActivityBinding.inflate(layoutInflater).also {
            it.progressBar.setContent {
                LinearDeterminateIndicator(viewModel)
            }
            it.topBar.setContent {
                WebviewTopBar(
                    onAddressChange = { url ->
                        viewModel.uiAction.invoke(WebViewUiAction.AddressChanged(url))
                    },
                    onLoadUrl = {
                        viewModel.uiAction.invoke(WebViewUiAction.LoadUrl)
                    },
                    url = viewModel.url.observeAsState().value.orEmpty(),
                    favicon = viewModel.favicon.observeAsState().value?.asImageBitmap()
                        ?: LocalContext.current.resources.getDrawable(R.drawable.kakao_logo, null).toBitmap()
                            .asImageBitmap()
                )
            }
            setContentView(it.root)
            it.bottomBar.setContent {
                WebviewBottomBar(viewModel)
            }
        }

        initView()
        observeViewModel()
    }

    private fun requestLocationPermission() {
        if (checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                this,
                ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION
                ),
                1
            )
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            navEvent.observe(this@MainActivity) {
                when (it) {
                    NavEvent.GoBack -> {
                        binding.webview.goBack()
                    }

                    NavEvent.GoForward -> {
                        binding.webview.goForward()
                    }

                    NavEvent.Refresh -> {
                        binding.webview.reload()
                    }

                    is NavEvent.LoadUrl -> {
                        binding.webview.loadUrl(url.value.orEmpty())
                    }
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            webview.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                webViewClient = object : LoggedWebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Log.d(TAG, "onPageStarted: $url")
                        viewModel.uiAction.invoke(WebViewUiAction.AddressChanged(url.orEmpty()))
                        super.onPageStarted(view, url, favicon)
                    }
                }
                webChromeClient = object : LoggedWebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        Log.d(TAG, "onProgressChanged: $newProgress")
                        viewModel.progressChanged.invoke(newProgress)
                        super.onProgressChanged(view, newProgress)
                    }

                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                        Log.d(TAG, "onReceivedIcon: $icon")
                        icon?.let {
                            viewModel.faviconReceived.invoke(it)
                        }
                        super.onReceivedIcon(view, icon)
                    }
                }
                // settings
                settings.javaScriptEnabled = true
                settings.setSupportMultipleWindows(false)
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.domStorageEnabled = true
                settings.allowContentAccess = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.builtInZoomControls = true
                settings.setNeedInitialFocus(false)
                settings.databaseEnabled = true
                settings.setGeolocationEnabled(true)
                settings.safeBrowsingEnabled = false
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                addJavascriptInterface(JsInterface(this@MainActivity), "test2")
                WebViewTransport()
                evaluateJavascript("test script", null)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) {
            viewModel.uiAction.invoke(WebViewUiAction.HistoryBack)
            return
        }
        super.onBackPressed()
    }


    class JsInterface(val con: Context) {
        @JavascriptInterface
        fun JsTest() {
            Toast.makeText(con, "JsTestToast", Toast.LENGTH_LONG).show()
        }
    }
}


