package com.example.webviewlifecycle

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

private const val TAG = "LoggedWebChromeClient"

open class LoggedWebChromeClient : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        Log.d(TAG, "onProgressChanged: $newProgress")
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        Log.d(TAG, "onReceivedTitle: $title")
        super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        Log.d(TAG, "onReceivedIcon: ")
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
        Log.d(TAG, "onReceivedTouchIconUrl: $url, $precomposed")
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        Log.d(TAG, "onShowCustomView: ")
        super.onShowCustomView(view, callback)
    }

    override fun onShowCustomView(view: View?, requestedOrientation: Int, callback: CustomViewCallback?) {
        Log.d(TAG, "onShowCustomView: $requestedOrientation")
        super.onShowCustomView(view, requestedOrientation, callback)
    }

    override fun onHideCustomView() {
        Log.d(TAG, "onHideCustomView: ")
        super.onHideCustomView()
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?,
    ): Boolean {
        Log.d(TAG, "onCreateWindow: $resultMsg, $isDialog, $isUserGesture, $resultMsg")
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onRequestFocus(view: WebView?) {
        Log.d(TAG, "onRequestFocus: ")
        super.onRequestFocus(view)
    }

    override fun onCloseWindow(window: WebView?) {
        Log.d(TAG, "onCloseWindow: ")
        super.onCloseWindow(window)
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        Log.d(TAG, "onJsAlert: url: $url, msg: $message, result: $result")
        return super.onJsAlert(view, url, message, result)
    }

    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?,
    ): Boolean {
        Log.d(TAG, "onJsBeforeUnload: url: $url, msg: $message, res: $result")
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?,
    ) {
        Log.d(TAG, "onGeolocationPermissionsShowPrompt: $origin")

        callback?.invoke(origin, true, false)
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        Log.d(TAG, "onGeolocationPermissionsHidePrompt: ")
        super.onGeolocationPermissionsHidePrompt()
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        Log.d(TAG, "onPermissionRequest: $request")
        super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        Log.d(TAG, "onPermissionRequestCanceled: $request")
        super.onPermissionRequestCanceled(request)
    }

    override fun onJsTimeout(): Boolean {
        Log.d(TAG, "onJsTimeout: ")
        return super.onJsTimeout()
    }

    override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        Log.d(TAG, "onConsoleMessage: $message, $lineNumber, $sourceID")
        super.onConsoleMessage(message, lineNumber, sourceID)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.d(TAG, "onConsoleMessage: $consoleMessage")
        return super.onConsoleMessage(consoleMessage)
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        Log.d(TAG, "getDefaultVideoPoster: ")
        return super.getDefaultVideoPoster()
    }

    override fun getVideoLoadingProgressView(): View? {
        Log.d(TAG, "getVideoLoadingProgressView: ")
        return super.getVideoLoadingProgressView()
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        Log.d(TAG, "getVisitedHistory: ")
        super.getVisitedHistory(callback)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?,
    ): Boolean {
        Log.d(TAG, "onShowFileChooser: $filePathCallback, $fileChooserParams")
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}
