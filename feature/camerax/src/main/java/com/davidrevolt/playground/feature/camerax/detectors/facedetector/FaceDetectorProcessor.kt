package com.davidrevolt.playground.feature.camerax.detectors.facedetector


import android.util.Log
import androidx.camera.view.PreviewView
import com.davidrevolt.playground.feature.camerax.detectors.DetectorOverlayEffect
import com.davidrevolt.playground.feature.camerax.detectors.DetectorProcessor
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor(previewView: PreviewView) : DetectorProcessor<Face>() {

    override val detector: FaceDetector // Passed to ML KIT
    override val effect: DetectorOverlayEffect<Face>?

    init {
        Log.i(TAG, "Init Face Detector Processor")
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
        detector = FaceDetection.getClient(faceDetectorOptions)
        effect = FaceOverlayEffect(previewView = previewView)
    }


}