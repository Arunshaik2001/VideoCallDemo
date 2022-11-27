package com.coder.videocalldemo

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.coder.videocalldemo.WebRTCUtil.callJavaScriptFunction
import com.coder.videocalldemo.WebRTCUtil.setupWebView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.*

lateinit var mainActivity: MainActivity

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            val permissionState =
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                    )
                )
            LaunchedEffect(key1 = true) {
                permissionState.launchMultiplePermissionRequest()
            }

            if (permissionState.allPermissionsGranted) {
                MainContent()
            }
        }
    }

}

@Composable
fun MainContent() {
    Scaffold(
        content = { MyContent() }
    )
}

@Composable
fun MyContent() {

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


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = {
            val layoutInflater: LayoutInflater = LayoutInflater.from(it)
            val view: View = layoutInflater.inflate(R.layout.activity_call, null, false)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            view
        }, update = { view ->
            val webView: WebView = view.findViewById(R.id.webView)
            webViewCompose = webView

            webView.loadUrl(mUrl)
            setupWebView(webView)
        })


        Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Image(
                    modifier = Modifier.clickable {
                        isAudio = !isAudio
                        callJavaScriptFunction(
                            webViewCompose!!,
                            "javascript:toggleAudio(\"$isAudio\")"
                        );
                    },
                    painter = painterResource(id = if (isAudio) R.drawable.btn_mute_normal else R.drawable.btn_unmute_normal),
                    contentDescription = "audio"
                )

                Image(
                    modifier = Modifier.clickable {
                        callJavaScriptFunction(
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
                        callJavaScriptFunction(
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
    MainContent()
}


