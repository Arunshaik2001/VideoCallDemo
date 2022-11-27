package com.coder.videocalldemo

import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.coder.videocalldemo.models.JavaScriptInterface
import java.util.*

object WebRTCUtil {


    fun setupWebView(webView: WebView) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavaScriptInterface(), "Android")
        loadVideoCall(webView)
    }

    fun loadVideoCall(webView: WebView) {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                initializePeer(webView)
            }
        }
    }


    fun callJavaScriptFunction(webView: WebView, function: String) {
        webView.post { webView.loadUrl(function) }
    }

    fun getUniqueId(): String {
        return UUID.randomUUID().toString()
    }


    fun initializePeer(webView: WebView) {
        val uniqueId = getUniqueId()
        callJavaScriptFunction(webView, "javascript:init('$uniqueId')")
        Log.i("initializePeer", uniqueId)
        callJavaScriptFunction(webView, "javascript:startCall('f7d33cf8-582e-4933-9311-a5ed9fe28d21')")
    }
}