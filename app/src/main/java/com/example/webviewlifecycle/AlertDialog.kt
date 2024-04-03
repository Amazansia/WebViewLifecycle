package com.example.webviewlifecycle

import android.webkit.JsResult

data class AlertDialog(
    val message: String,
    val result: JsResult? = null,
)
