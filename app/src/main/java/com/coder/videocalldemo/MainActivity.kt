package com.coder.videocalldemo

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

            var uniqueId by remember {
                mutableStateOf("")
            }

            var joinCall by remember {
                mutableStateOf(false)
            }

            var callType by remember {
                mutableStateOf<CallType?>(null)
            }

            if (!joinCall) {
                JoinCallCompose {id,type ->
                    joinCall = true
                    Log.i("JoinCallCompose", id+callType.toString())
                    uniqueId = id
                    callType = type
                }
            } else {
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
                    VideoCallCompose(uniqueId,callType)
                }
            }
        }
    }

}


@Composable
fun JoinCallCompose(onClick: (String,CallType) -> Unit) {
    var idField by remember {
        mutableStateOf(TextFieldValue())
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        Button(onClick = { onClick("",CallType.CREATE) }) {
            Text(text = "Create Room")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = idField, onValueChange = {
            idField = it
        }, label = {
            Text("Enter UniqueId")
        })

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onClick(idField.text,CallType.JOIN) }) {
            Text(text = "Join Room")
        }
    }
}




