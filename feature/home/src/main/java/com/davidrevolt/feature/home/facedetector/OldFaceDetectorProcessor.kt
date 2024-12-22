package com.davidrevolt.feature.home.facedetector

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class OldFaceDetectorProcessor {

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


    // Not in use because replaced by MlKitAnalyzer Class
    fun stop() {
        detector.close()
    }

    // Not in use because replaced by MlKitAnalyzer Class
    @OptIn(ExperimentalGetImage::class)
    fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let { image ->
            detectInImage(InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees))
                .addOnSuccessListener { results ->
                    onSuccess(results,imageProxy.imageInfo.timestamp)
                    imageProxy.close()
                }
                .addOnFailureListener {
                    onFailure(it)
                    imageProxy.close()
                }
        }
    }

    // Not in use because replaced by MlKitAnalyzer Class
    private fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    fun onSuccess(faces: List<Face>, frameTimestamp: Long) {
        Log.d("ImageAnalysis", "Face Detected: ${faces.size}")
        /*        for (face in faces) {
                    //graphicOverlay.add(FaceGraphic(graphicOverlay, face))
                }*/
    }

    fun onFailure(e: Exception) {
        Log.e("ImageAnalysis", "Face detection failed $e")
    }

}