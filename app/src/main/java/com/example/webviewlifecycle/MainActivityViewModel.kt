package com.example.webviewlifecycle

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    private val _navEvent = MutableStateFlow<NavEvent>(NavEvent.Init)
    val navEvent: StateFlow<NavEvent> = _navEvent

    private val _url = MutableStateFlow("https://www.daum.net/")
    val url: StateFlow<String> = _url

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    val progressChanged: (Int) -> Unit = { num ->
        _progress.value = num
    }

    private var _favicon = MutableStateFlow(BitmapPainter(ImageBitmap(50, 50)))
    val favicon: StateFlow<BitmapPainter> = _favicon

    val faviconReceived: (BitmapPainter) -> Unit = { favicon ->
        _favicon.value = favicon
    }

    val uiAction: (WebViewUiAction) -> Unit = { action ->
        when (action) {
            WebViewUiAction.HistoryBack -> {
                _navEvent.value = NavEvent.GoBack
            }

            WebViewUiAction.HistoryForward -> {
                _navEvent.value = NavEvent.GoForward
            }

            WebViewUiAction.RefreshPressed -> {
                _navEvent.value = NavEvent.Refresh
            }

            is WebViewUiAction.AddressChanged -> {
                _url.value = action.url
            }

            WebViewUiAction.LoadUrl -> {
                _navEvent.value = NavEvent.LoadUrl(url.value.orEmpty())
            }
        }
    }

    init {
        uiAction.invoke(WebViewUiAction.LoadUrl)
    }
}

sealed class WebViewUiAction {
    object HistoryForward : WebViewUiAction()
    object HistoryBack : WebViewUiAction()
    object RefreshPressed : WebViewUiAction()
    object LoadUrl : WebViewUiAction()
    data class AddressChanged(val url: String) : WebViewUiAction()
}


sealed class NavEvent {
    object Init : NavEvent()
    object GoBack : NavEvent()
    object Refresh : NavEvent()
    object GoForward : NavEvent()
    data class LoadUrl(val url: String) : NavEvent()
}
