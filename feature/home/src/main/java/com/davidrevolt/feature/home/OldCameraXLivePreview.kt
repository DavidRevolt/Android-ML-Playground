package com.davidrevolt.feature.home

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.davidrevolt.feature.home.facedetector.OldFaceDetectorProcessor
import com.google.mlkit.common.MlKitException

// Using cameraProvider
/*
A CameraController provides most of the CameraX core functionality in a single class.
It requires little setup code, and it automatically handles camera initialization, use case management, target rotation, tap-to-focus, pinch-to-zoom, and more.

A CameraProvider is still easy-to-use, but since the app developer handles more of the setup,
there are more opportunities to customize the configuration,
like enabling output image rotation or setting the output image format in ImageAnalysis.
You can also use a custom Surface for camera preview allows for more flexibility,
whereas with CameraController you are required to use a PreviewView.
Using your existing Surface code could be useful if it's already an input to other parts of your app.
*/

@Composable
fun OldCameraXLivePreview() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val cameraProvider = ProcessCameraProvider.getInstance(context).get()

    // Preview Binding
    // previewView: Custom View that displays the camera feed for CameraX's Preview use case.
    // The image preview streams to a surface inside the PreviewView when the camera becomes active.
    val previewView = PreviewView(context)
    val previewUseCase = Preview.Builder().build()
        .also { it.surfaceProvider = previewView.surfaceProvider }

    // Analysis
    val executor = context.mainExecutor
    var imageProcessor = OldFaceDetectorProcessor()
    val analysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    analysisUseCase.setAnalyzer(
        executor,
        ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
            try {
                imageProcessor.processImageProxy(imageProxy)
            } catch (e: MlKitException) {
                Log.e("ImageAnalysis", "Failed to process image. Error: " + e.localizedMessage)
            }
        }

    )


    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, previewUseCase, analysisUseCase)

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())


}

