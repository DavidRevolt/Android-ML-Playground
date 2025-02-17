package com.davidrevolt.feature.home.facedetector


import android.util.Log
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import com.davidrevolt.feature.home.DetectorOverlayEffect
import com.davidrevolt.feature.home.MlKitImageProcessor
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor(previewView: PreviewView) : MlKitImageProcessor {

    override val detector: FaceDetector // Passed to ML KIT
    override val effect: DetectorOverlayEffect<Face>  // Passed to  cameraController.setEffects

    init {
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
        detector = FaceDetection.getClient(faceDetectorOptions)
        effect = FaceOverlayEffect(previewView)
    }


    override fun processMlKitAnalyzerResult(mlkitResult: MlKitAnalyzer.Result?) {
        var detections = mlkitResult?.getValue(detector)// e.g: List of faces
        if (detections == null)
            onFailure(mlkitResult?.getThrowable(detector))
        else
            onSuccess(detections = detections, frameTimestamp = mlkitResult!!.timestamp)
    }

    // gets the ML KIT Result [for frame] and draw on the same frame using the timestamp
    private fun onSuccess(detections: List<Face>, frameTimestamp: Long) {
        Log.d("ImageAnalysis", "Detected: ${detections.size} Instances")
        effect.drawEffect(detections, frameTimestamp)
    }

    private fun onFailure(throwable: Throwable?) {
        Log.e(
            "ImageAnalysis",
            "Detection failed ${throwable?.cause.toString()}"
        )
    }

}