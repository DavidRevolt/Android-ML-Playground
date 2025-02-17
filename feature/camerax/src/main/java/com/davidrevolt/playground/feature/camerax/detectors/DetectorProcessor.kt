package com.davidrevolt.playground.feature.camerax.detectors

import android.util.Log
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.google.mlkit.vision.interfaces.Detector

/*
* Base class to init MLKIT Detector and OverLay Effect
* T - The detected object, e.g: Face/DetectedObject
* */
abstract class DetectorProcessor<T>() {
    abstract val detector: Detector<*> // e.g FaceDetector
    abstract val effect: DetectorOverlayEffect<T>? // e.g DetectorOverlayEffect<Face>

    fun processMlKitAnalyzerResult(mlkitResult: MlKitAnalyzer.Result?){
        var detections = mlkitResult?.getValue(detector)// e.g: List of faces
        if (detections == null)
            onFailure(mlkitResult?.getThrowable(detector))
        else
            onSuccess(detections = detections as List<T> , frameTimestamp = mlkitResult!!.timestamp)
    }

    // gets the ML KIT Result [for frame] and draw on the same frame using the timestamp
    protected open fun onSuccess(detections: List<T>, frameTimestamp: Long) {
        Log.i("ImageAnalysis", "Detected: ${detections.size} Instances")
        effect?.drawEffect(detections, frameTimestamp)
    }

    protected open fun onFailure(throwable: Throwable?) {
        Log.e(
            "ImageAnalysis",
            "Detection failed ${throwable?.cause.toString()}"
        )
    }


    fun stop(){
        Log.i("ImageAnalysis", "Stopping Detector")
        detector.close()
    }

}