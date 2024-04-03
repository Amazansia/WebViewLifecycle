package com.example.webviewlifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _navEvent = MutableLiveData<NavEvent>()
    val navEvent: LiveData<NavEvent> = _navEvent

    private val _url = MutableLiveData<String>()
    val url: LiveData<String> = _url

    fun uiAction(action: WebViewUiAction) {
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
        }
    }

//    val uiAction: (WebViewUiAction) -> Unit = { action ->
//        when (action) {
//            WebViewUiAction.HistoryBack -> {
//                _navEvent.value = NavEvent.GoBack
//            }
//
//            WebViewUiAction.HistoryForward -> {
//                _navEvent.value = NavEvent.GoForward
//            }
//
//            WebViewUiAction.RefreshPressed -> {
//                _navEvent.value = NavEvent.Refresh
//            }
//
//            is WebViewUiAction.AddressChanged -> {
//                _url.value = action.url
//            }
//        }
//    }
}

sealed class WebViewUiAction() {
    object HistoryForward : WebViewUiAction()
    object HistoryBack : WebViewUiAction()
    object RefreshPressed : WebViewUiAction()
    data class AddressChanged(val url: String) : WebViewUiAction()
}


sealed class NavEvent() {
    object GoBack : NavEvent()
    object Refresh : NavEvent()
    object GoForward : NavEvent()
}
