package com.davidrevolt.feature.home

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCameraPermissionGranted: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
           RequestCameraAndMicPermissions() {
               onCameraPermissionGranted.invoke()
           }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestCameraAndMicPermissions(ifPermissionsGranted: @Composable () -> Unit = {}) {

    val cameraPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    if (cameraPermissionsState.allPermissionsGranted) {
        ifPermissionsGranted.invoke()
    } else {
        Column {
            val textToShow = if (cameraPermissionsState.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}