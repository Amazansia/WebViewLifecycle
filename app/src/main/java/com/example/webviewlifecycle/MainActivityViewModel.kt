package com.example.webviewlifecycle

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    // stateflow: 화면 전환할 때마다 emit
    // 이벤트에 더 적합한 flow는 sharedFlow
    // TODO: 화면 회전시 하얀화면으로 변경됨...
    private val _navEvent = MutableStateFlow<NavEvent>(NavEvent.Init)
    val navEvent: StateFlow<NavEvent> = _navEvent

    private val _url = MutableStateFlow("https://www.daum.net/")
    val url: StateFlow<String> = _url

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    // 아래 uiAction하고 사용 의도가 살짝 다름
    val progressChanged: (Int) -> Unit = { num ->
        _progress.value = num
    }

    // bitmap 형식의 데이터만 사용하도록 or nullable
    // null 처리에 대한 로직이 필요하기 때문에 nullable로 해야 함
    // 아니면 래핑클래스를 만들거나...
    private var _favicon: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val favicon: StateFlow<Bitmap?> = _favicon

    val faviconReceived: (Bitmap?) -> Unit = { favicon ->
        _favicon.value = favicon
    }

    // 액션에 대한 정보를 저장
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
                _navEvent.value = NavEvent.LoadUrl(url.value)
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
