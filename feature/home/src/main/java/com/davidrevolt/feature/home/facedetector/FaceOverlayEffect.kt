package com.davidrevolt.feature.home.facedetector

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraEffect
import androidx.camera.effects.OverlayEffect
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.face.Face


class FaceOverlayEffect(val previewView: PreviewView) {

    private var faces: List<Face>? = null
    private var timestamp = 0L

    val boundingRectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.YELLOW
        strokeWidth = 5F
        alpha = 200
    }

    val overlayEffect =
        OverlayEffect(CameraEffect.PREVIEW, 5, Handler(Looper.getMainLooper()), {}).also { it ->
            it.setOnDrawListener { frame ->
                if (frame.timestampNanos != timestamp) {
                    // Do not change the drawing if the frame doesn’t match the analysis result.
                    return@setOnDrawListener true
                }
                frame.overlayCanvas.drawColor(
                    Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR
                ) // CLEARING PREVIOUS DRAWING
                faces?.let { //Draw Again
                    // Using sensor coordinates to draw.
                    val sensorToUi = previewView.sensorToViewTransform
                    val sensorToEffect = frame.sensorToBufferTransform
                    val uiToSensor = Matrix()
                    sensorToUi?.invert(uiToSensor)
                    uiToSensor.postConcat(sensorToEffect)
                    frame.overlayCanvas.setMatrix(uiToSensor)

                    it.forEach { face ->
                        frame.overlayCanvas.drawRect(face.boundingBox, boundingRectPaint)
                    }
                }
                true
            }
        }


    fun drawEffect(faces: List<Face>, frameTimestamp: Long) {
        this.faces = faces
        this.timestamp = frameTimestamp
        overlayEffect.drawFrameAsync(timestamp)
    }
}