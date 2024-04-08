package com.example.webviewlifecycle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    // stateFlow

    private val _navEvent = MutableLiveData<NavEvent>()
    val navEvent: LiveData<NavEvent> = _navEvent

    private val _url = MutableLiveData("https://www.daum.net/")
    val url: LiveData<String> = _url

    private val _webSchemeUrl = MutableLiveData("https://www.daum.net/")
    val webSchemeUrl: LiveData<String> = _webSchemeUrl

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    val progressChanged: (Int) -> Unit = { num ->
        _progress.value = num
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

            is WebViewUiAction.HttpAddressUpdated -> {
                _webSchemeUrl.value = action.url
                Log.d(TAG, "httpUrl: ${webSchemeUrl.value}: ")
            }
        }
    }
}

sealed class WebViewUiAction {
    object HistoryForward : WebViewUiAction()
    object HistoryBack : WebViewUiAction()
    object RefreshPressed : WebViewUiAction()
    data class AddressChanged(val url: String) : WebViewUiAction()
    data class HttpAddressUpdated(val url: String) : WebViewUiAction()
}


sealed class NavEvent {
    object GoBack : NavEvent()
    object Refresh : NavEvent()
    object GoForward : NavEvent()
}
