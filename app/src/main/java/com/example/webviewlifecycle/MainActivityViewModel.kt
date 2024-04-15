package com.example.webviewlifecycle

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent = _navEvent.asSharedFlow()

    private val _url = MutableStateFlow("https://map.kakao.com/")
    val url: StateFlow<String> = _url

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    /*
    아래 uiAction하고 사용 의도가 살짝 다름 - 함수형 변수를 쓸 필요 없음
    val progressChanged: (Int) -> Unit = { num ->
        _progress.value = num
    }
    */

    fun progressChanged(num: Int) {
        _progress.value = num
    }

    private var _favicon: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val favicon: StateFlow<Bitmap?> = _favicon

    val faviconReceived: (Bitmap?) -> Unit = { favicon ->
        _favicon.value = favicon
    }

    private fun event(event: NavEvent) {
        viewModelScope.launch {
            _navEvent.emit(event)
        }
    }

    // 액션에 대한 정보를 저장
    val uiAction: (WebViewUiAction) -> Unit = { action ->
        when (action) {
            WebViewUiAction.HistoryBack -> {
                event(NavEvent.GoBack)
            }

            WebViewUiAction.HistoryForward -> {
                event(NavEvent.GoForward)
            }

            WebViewUiAction.RefreshPressed -> {
                event(NavEvent.Refresh)
            }

            is WebViewUiAction.AddressChanged -> {
                _url.value = action.url
            }

            WebViewUiAction.LoadUrl -> {
                event(NavEvent.LoadUrl(url.value))
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
    object GoBack : NavEvent()
    object Refresh : NavEvent()
    object GoForward : NavEvent()
    data class LoadUrl(val url: String) : NavEvent()
}
