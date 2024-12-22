package com.davidrevolt.feature.home

import android.util.Log
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
import com.davidrevolt.feature.home.facedetector.FaceOverlayEffect


// Using CameraController Class
@Composable
fun CameraXLivePreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = PreviewView(context)
    var imageProcessor = FaceDetectorProcessor()

    val lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
    var cameraController = LifecycleCameraController(context)
    cameraController.cameraSelector = lensFacing
    previewView.controller = cameraController // Connect CameraController to preview


    // EFFECT - FACE DRAWING
    val faceOverlayEffect = FaceOverlayEffect(previewView = previewView)
    cameraController.setEffects(setOf(faceOverlayEffect.overlayEffect))


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
        ) { result: MlKitAnalyzer.Result? ->
            var faces = result?.getValue(imageProcessor.detector)//Face List
            if (faces == null) {
                Log.e(
                    "ImageAnalysis",
                    "Face detection failed ${result?.getThrowable(imageProcessor.detector)?.cause.toString()}"
                )
            } else {
                Log.d("ImageAnalysis", "Face Detected: ${faces.size}")
                faceOverlayEffect.drawEffect(faces = faces, frameTimestamp = result!!.timestamp)
            }
        }
    )
    cameraController.bindToLifecycle(lifecycleOwner) //Because cameraController is Lifecycle aware
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

