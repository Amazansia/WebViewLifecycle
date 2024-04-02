@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.webviewlifecycle

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webviewlifecycle.ui.theme.WebviewLifecycleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebviewLifecycleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(
                        bottomBar = { WebviewBottomBar() },
                        topBar = { WebviewTopBar() }
                    ) {
                        AndroidTestWebView(it)
                    }
                }
            }
        }
    }
}

@Composable
fun WebviewBottomBar() {
    BottomAppBar {
        BottomBarBackButton()
        BottomBarForwardButton()
        BottomBarRefreshButton()
        // 기타기능 버튼: 링크 복사, 다른 앱으로 공유, 다른 브라우저로 열기...(생략)
    }
}

@Composable
fun BottomBarRefreshButton() {
//    TODO("Not yet implemented")
}

@Composable
fun BottomBarForwardButton() {
//    TODO("Not yet implemented")
}

@Composable
fun BottomBarBackButton() {
    IconButton(onClick = { }) {

    }
}

@Composable
fun WebviewTopBar() {
    TopAppBar(
        navigationIcon = { TopBarNaviIcon() },
        title = { WebviewAddressBar() },
        actions = {
            // 카카오톡으로 공유
            // 둥둥이로 변환
        }
    )
}

@Composable
fun TopBarNaviIcon() {
//    TODO("Not yet implemented")
}

@Composable
fun WebviewAddressBar() {
//    TODO("Not yet implemented")
}

@Composable
fun AndroidTestWebView(paddingValues: PaddingValues) {
    Column {
        LinearDeterminateIndicator()
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    // width, height 값 고정: 애니메이션 시 해당 사이즈를 기준으로 계산하도록
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    isFocusable = true
                    isFocusableInTouchMode = true

                    webViewClient = LoggedWebViewClient()
                    webChromeClient = WebChromeClient()
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
                }
            },
            update = { webView ->
                webView.loadUrl("https://www.daum.net/")
                Log.d(TAG, webView.progress.toString())
            }
        )

    }
}

@Composable
fun LinearDeterminateIndicator() {
    var currentProgress: Float by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            loading = true
            scope.launch {
                loadProgress { progress ->
                    currentProgress = progress
                }
                loading = false // Reset loading when the coroutine finishes
            }
        }, enabled = !loading) {
            Text("Start loading")
        }

        if (loading) {
            LinearProgressIndicator(
                progress = currentProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Iterate the progress value */
suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(100)
    }
}


@Preview(showBackground = true)
@Composable
fun AndroidWebviewPreview() {
    WebviewLifecycleTheme {
//        AndroidTestWebView()
    }
}


