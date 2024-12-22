package com.davidrevolt.feature.home.facedetector

import android.util.Log
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor(){

    // Not private for MlKitAnalyzer() in CameraPreviewScreen2
     val detector: FaceDetector

    init {
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
        detector = FaceDetection.getClient(faceDetectorOptions)
    }


    // gets the MLKIT Result [for frame] and draw on the same frame using the timestamp
     fun onSuccess(faces: List<Face>, frameTimestamp: Long) {
        Log.d("ImageAnalysis", "Face Detected: ${faces.size}")

    }

     fun onFailure(e: Exception) {
        Log.e("ImageAnalysis", "Face detection failed $e")
    }


}