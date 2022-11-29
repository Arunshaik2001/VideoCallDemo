package com.coder.videocalldemo

import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.coder.videocalldemo.models.JavaScriptInterface
import java.util.*

enum class CallType{
    CREATE,
    JOIN
}

object WebRTCUtil {

    var yourUniqueId = ""
    var callType: CallType? = null

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

    private fun loadVideoCall(webView: WebView) {
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
        if(callType == CallType.CREATE){
            if(yourUniqueId.isEmpty()) {
                yourUniqueId = getUniqueId()
            }
            callJavaScriptFunction(webView, "javascript:init('$yourUniqueId')")
            Log.i("initializePeer", yourUniqueId)
            callJavaScriptFunction(webView, "javascript:startCall('$yourUniqueId')")
        }
        else{
            if(yourUniqueId.isEmpty()) {
                yourUniqueId = getUniqueId()
            }
            callJavaScriptFunction(webView, "javascript:init('${getUniqueId()}')")
            Log.i("initializePeer", yourUniqueId)
            callJavaScriptFunction(webView, "javascript:startCall('$yourUniqueId')")
        }
    }
}