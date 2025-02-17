package com.davidrevolt.playground.feature.camerax.detectors.objectdetector

import android.util.Log
import androidx.camera.view.PreviewView
import com.davidrevolt.playground.feature.camerax.detectors.DetectorOverlayEffect
import com.davidrevolt.playground.feature.camerax.detectors.DetectorProcessor
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectorProcessor(previewView: PreviewView) : DetectorProcessor<DetectedObject>() {
    override val detector: ObjectDetector
    override val effect: DetectorOverlayEffect<DetectedObject>?

    init {
        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        detector = ObjectDetection.getClient(options)
        effect = null
    }

    override fun onSuccess(detections: List<DetectedObject>, frameTimestamp: Long) {
        super.onSuccess(detections, frameTimestamp)
        //   Log.d("Tes0t","Detected: ${detections.size} Instances")
        detections.forEach { detectedObject ->
            detectedObject.labels.forEach { label ->
                Log.d("Tes0t", label.text)
            }
        }
    }
}