package com.davidrevolt.playground.feature.camerax.detectors

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraEffect
import androidx.camera.effects.Frame
import androidx.camera.effects.OverlayEffect
import androidx.camera.view.PreviewView

/*
* abstract class to init overlayEffect
* overlayEffect passed to cameraX cameraController.setEffects() to draw on frame [using timestamp]
* Implement drawOnDetections method to draw the detections on the current frame being proceed
* */

abstract class DetectorOverlayEffect<T>(private val previewView: PreviewView) {

    private var detections: List<T> = emptyList()
    private var frameTimestamp = 0L

    // overlayEffect - Use to draw overlay! passed to cameraController.setEffects()
    val overlayEffect =
        OverlayEffect(CameraEffect.PREVIEW, 5, Handler(Looper.getMainLooper()), {}).also { it ->
            it.setOnDrawListener { frame ->
                if (frame.timestampNanos != frameTimestamp) {
                    // Do not change the drawing if the frame doesn’t match the analysis result.
                    return@setOnDrawListener true
                }
                // CLEARING PREVIOUS DRAWING FOR THIS overlayEffect
                frame.overlayCanvas.drawColor(
                    Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR
                )
                detections.let { //Draw Again
                    // Using sensor coordinates to draw.
                    // The goal is to line up the camera’s real view with the overlay you’re drawing on
                    val sensorToUi = previewView.sensorToViewTransform
                    val sensorToEffect = frame.sensorToBufferTransform
                    val uiToSensor = Matrix()
                    sensorToUi?.invert(uiToSensor)
                    uiToSensor.postConcat(sensorToEffect)
                    frame.overlayCanvas.setMatrix(uiToSensor)
                    drawOnDetections(it, frame) // Abstract fun should be implemented what to draw
                }
                true
            }
        }

    abstract fun drawOnDetections(detections: List<T>, frame: Frame)

    fun drawEffect(detections: List<T>, frameTimestamp: Long) {
        this.detections = detections
        this.frameTimestamp = frameTimestamp
        overlayEffect.drawFrameAsync(this.frameTimestamp) // Trigger setOnDrawListener
    }
}