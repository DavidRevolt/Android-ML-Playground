package com.davidrevolt.playground.feature.camerax.detectors.customobjectdetector

import android.util.Log
import androidx.camera.view.PreviewView
import com.davidrevolt.playground.feature.camerax.detectors.DetectorOverlayEffect
import com.davidrevolt.playground.feature.camerax.detectors.DetectorProcessor
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class CustomObjectDetectorProcessor(previewView: PreviewView) : DetectorProcessor<DetectedObject>() {
    override val detector: ObjectDetector
    override val effect: DetectorOverlayEffect<DetectedObject>?

    init {
        Log.i(TAG, "Init Custom Object Detector Processor")
        val localModel = LocalModel.Builder()
            .setAssetFilePath("custom_models/efficientnet.tflite")
            .build()

        val options = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .enableMultipleObjects()
            .setClassificationConfidenceThreshold(0.4f)
            .setMaxPerObjectLabelCount(5) //  If not set, the default value of 10 will be used.
            .build()
        detector = ObjectDetection.getClient(options)
        effect =  CustomObjectOverlayEffect(previewView = previewView)
    }


}