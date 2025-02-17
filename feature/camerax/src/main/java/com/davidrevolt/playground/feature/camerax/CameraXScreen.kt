package com.davidrevolt.playground.feature.camerax

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun CameraXScreen(
    viewModel: CameraXViewModel = hiltViewModel()
) {
    val uiState by viewModel.cameraXUiState.collectAsStateWithLifecycle()

    when(uiState){
        is CameraXUiState.Ready -> {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val previewView = PreviewView(context)
            viewModel.bindPreviewUseCase(previewView, lifecycleOwner)
            viewModel.bindAnalysisUseCase(previewView)
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())


        }
        else -> {}
    }

}