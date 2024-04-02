package com.example.webviewlifecycle

import android.os.Bundle
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.webviewlifecycle.databinding.MainActivityBinding

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    lateinit var binding: MainActivityBinding
    val viewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater).also {
            it.topBar.setContent {
                WebviewTopBar(viewModel)
            }
            setContentView(it.root)
            it.bottomBar.setContent {
                WebviewBottomBar(viewModel)
            }
        }
        initView()
        observeViewModel()
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
                }
            }
            url.observe(this@MainActivity) {
                binding.webview.loadUrl(it)
            }
        }
    }

    private fun initView() {
        with(binding) {
            webview.apply {
                isFocusable = true
                isFocusableInTouchMode = true

                webViewClient = LoggedWebViewClient()
                webChromeClient = LoggedWebChromeClient()

                // settings
                settings.javaScriptEnabled = true
                settings.setSupportMultipleWindows(true)
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
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                loadUrl("https://www.daum.net/")
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
}


