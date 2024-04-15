package com.example.webviewlifecycle

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.webviewlifecycle.databinding.MainActivityBinding
import kotlinx.coroutines.launch


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var binding: MainActivityBinding
    private val viewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater).also {
            it.progressBar.setContent {
                val progress = viewModel.progress.collectAsStateWithLifecycle().value
                LinearDeterminateIndicator(progress = progress)
            }
            it.topBar.setContent {
                WebviewTopBar(
                    onAddressChange = { url ->
                        viewModel.uiAction.invoke(WebViewUiAction.AddressChanged(url))
                    },
                    onLoadUrl = {
                        viewModel.uiAction.invoke(WebViewUiAction.LoadUrl)
                    },
                    url = viewModel.url.collectAsStateWithLifecycle().value,
                    favicon = viewModel.favicon.collectAsStateWithLifecycle().value,
                )
            }
            setContentView(it.root)

            it.bottomBar.setContent {
                WebviewBottomBar(
                    onHistoryBack = { onUIAction(WebViewUiAction.HistoryBack) },
                    onHistoryForward = { onUIAction(WebViewUiAction.HistoryForward) },
                    onRefreshPressed = { onUIAction(WebViewUiAction.RefreshPressed) }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.navEvent.collect { event ->
                when (event) {
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
                        binding.webview.loadUrl(event.url)
                    }
                }
            }
        }

        initView()
    }

    private fun onUIAction(action: WebViewUiAction) {
        viewModel.uiAction.invoke(action)
    }

    private fun requestLocationPermission(): Boolean {
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
        return checkSelfPermission(
            applicationContext,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(
            applicationContext,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(
            applicationContext,
            ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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
                        viewModel.progressChanged(newProgress)
                        super.onProgressChanged(view, newProgress)
                    }

                    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                        Log.d(TAG, "onReceivedIcon: $icon")
                        icon?.let {
                            viewModel.faviconReceived.invoke(it)
                        }
                        super.onReceivedIcon(view, icon)
                    }

                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String?,
                        callback: GeolocationPermissions.Callback?,
                    ) {
                        Log.d(TAG, "onGeolocationPermissionsShowPrompt: $origin")
                        super.onGeolocationPermissionsShowPrompt(origin, callback)
                        callback?.invoke(origin, requestLocationPermission(), false)
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

                // 받는 것
                addJavascriptInterface(JsInterface(this@MainActivity), "test2")
                WebViewTransport()
                // 실행하는 것
                evaluateJavascript("test script", null)
            }
                .setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    val request = DownloadManager.Request(Uri.parse(url))
                    request.setMimeType(mimetype)
                    request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
                    request.addRequestHeader("User-Agent", userAgent)
                    request.setDescription("Downloading file...")

                    // contentDisposition에 파일명 정보가 있을 때는 아래 널체크가 생략됨
                    val pattern = """filename="([^"]+\.\w+)";""".toRegex()
                    val matchResult = pattern.find(contentDisposition)
                    var fileNameWithExtension = matchResult?.groups?.get(1)?.value
                    if (fileNameWithExtension == null) {
                        // url에 파일명 정보가 있을 때
                        fileNameWithExtension = contentDisposition.replace("attachment; filename=", "")
                        if (fileNameWithExtension.isNotEmpty()) {
                            val idxFileName = fileNameWithExtension.indexOf("filename=")
                            if (idxFileName > -1) {
                                fileNameWithExtension =
                                    fileNameWithExtension.substring(idxFileName + 9).trim { it <= ' ' }
                            }
                            if (fileNameWithExtension.endsWith(";")) {
                                fileNameWithExtension =
                                    fileNameWithExtension.substring(0, fileNameWithExtension.length - 1)
                            }
                            if (fileNameWithExtension.startsWith("\"") && fileNameWithExtension.startsWith("\"")) {
                                fileNameWithExtension =
                                    fileNameWithExtension.substring(1, fileNameWithExtension.length - 1)
                            }
                        } else {
                            // 정말 알수없음
                            fileNameWithExtension = URLUtil.guessFileName(url, contentDisposition, mimetype)
                        }
                    }

                    request.setTitle(fileNameWithExtension)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        fileNameWithExtension
                    )
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(
                        applicationContext,
                        "Download File: $fileNameWithExtension",
                        Toast.LENGTH_LONG
                    ).show()
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
