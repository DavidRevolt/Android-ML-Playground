package com.davidrevolt.feature.home

import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.davidrevolt.feature.home.facedetector.FaceOverlayEffect
import com.google.mlkit.vision.interfaces.Detector

/*
Using both Interface and Abstract Class because If we only use one of them
in CameraXLivePreview we will need to specify what is T - THIS BREAK GENERICS


abstract class DetectorProcessor<T> : MlKitImageProcessor {
    // gets the ML KIT Result [for frame] and draw on the same frame using the timestamp
    protected abstract fun onSuccess(detections: List<T>, frameTimestamp: Long)
    protected abstract fun onFailure(throwable: Throwable?)
}
*/

interface MlKitImageProcessor {
    // A property declared in an interface can either be abstract or provide implementations for accessors.
    val detector: Detector<*>
    val effect: DetectorOverlayEffect<*>
    fun processMlKitAnalyzerResult(mlkitResult: MlKitAnalyzer.Result?)
}