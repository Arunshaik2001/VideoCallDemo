package com.coder.videocalldemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoCallCompose(uniqueId: String,callType: CallType?) {
    Scaffold(
        content = { MyContent(uniqueId,callType) }
    )
}

@Composable
fun MyContent(uniqueId: String,callType: CallType?) {

    val mUrl = "file:android_asset/call.html"
    var isAudio by remember {
        mutableStateOf(true)
    }
    var isVideo by remember {
        mutableStateOf(true)
    }

    var webViewCompose: WebView? by remember {
        mutableStateOf(null)
    }

    val peerId by remember {
        mutableStateOf(uniqueId.ifEmpty { WebRTCUtil.getUniqueId() })
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = {
            val layoutInflater: LayoutInflater = LayoutInflater.from(it)
            val view: View = layoutInflater.inflate(R.layout.activity_call, null, false)
            view
        }, update = { view ->
            val webView: WebView = view.findViewById(R.id.webView)
            webViewCompose = webView

            webView.loadUrl(mUrl)
            WebRTCUtil.yourUniqueId = peerId
            WebRTCUtil.callType = callType
            Log.i("WebRTCUtil.yourUniqueId", WebRTCUtil.yourUniqueId)
            WebRTCUtil.setupWebView(webView)
        })


        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = peerId, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Image(
                    modifier = Modifier.clickable {
                        isAudio = !isAudio
                        WebRTCUtil.callJavaScriptFunction(
                            webViewCompose!!,
                            "javascript:toggleAudio(\"$isAudio\")"
                        );
                    },
                    painter = painterResource(id = if (isAudio) R.drawable.btn_mute_normal else R.drawable.btn_unmute_normal),
                    contentDescription = "audio"
                )

                Image(
                    modifier = Modifier.clickable {
                        WebRTCUtil.callJavaScriptFunction(
                            webViewCompose!!,
                            "javascript:disconnectCall()"
                        );
                        mainActivity.finish()
                    },
                    painter = painterResource(id = R.drawable.btn_endcall_normal),
                    contentDescription = "end"
                )

                Image(
                    modifier = Modifier.clickable {
                        isVideo = !isVideo
                        WebRTCUtil.callJavaScriptFunction(
                            webViewCompose!!,
                            "javascript:toggleVideo(\"$isVideo\")"
                        );
                    },
                    painter = painterResource(id = if (isVideo) R.drawable.btn_video_muted else R.drawable.btn_video_normal),
                    contentDescription = "video"
                )
            }
        }

    }


}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VideoCallCompose("",CallType.CREATE)
}