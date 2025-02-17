package com.davidrevolt.feature.home

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.davidrevolt.feature.home.facedetector.FaceDetectorProcessor


// Using CameraController Class
@Composable
fun CameraXLivePreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = PreviewView(context)

    var cameraController = LifecycleCameraController(context)
    val lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
    cameraController.cameraSelector = lensFacing
    previewView.controller = cameraController // Connect CameraController to preview


    // Setting Img Detector and EFFECT - DRAWING
    var imageProcessor: MlKitImageProcessor  = FaceDetectorProcessor(previewView)
    cameraController.setEffects(setOf(imageProcessor.effect.overlayEffect))


    // IMG ANALYSIS
    // This guarantees only one image will be delivered for analysis at a time
    cameraController.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
    val executor = context.mainExecutor
    cameraController.setImageAnalysisAnalyzer(
        executor,
        MlKitAnalyzer(
            listOf(imageProcessor.detector),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            executor
        ) { mlkitResult: MlKitAnalyzer.Result? ->
            imageProcessor.processMlKitAnalyzerResult(mlkitResult = mlkitResult)
        }
    )
    cameraController.bindToLifecycle(lifecycleOwner) //Because cameraController is Lifecycle aware
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

}

