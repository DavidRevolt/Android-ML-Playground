package com.davidrevolt.playground.feature.camerax.detectors.customobjectdetector

import android.graphics.Color
import android.graphics.Paint
import androidx.camera.effects.Frame
import androidx.camera.view.PreviewView
import com.davidrevolt.playground.feature.camerax.detectors.DetectorOverlayEffect
import com.google.mlkit.vision.objects.DetectedObject
import java.util.Locale
import kotlin.math.max


class CustomObjectOverlayEffect(previewView: PreviewView) :
    DetectorOverlayEffect<DetectedObject>(previewView) {

    override fun drawOnDetections(detections: List<DetectedObject>, frame: Frame) {
        detections.forEachIndexed { ind, detectedObject ->
            val boundingRectPaint = Paint().apply {
                style = Paint.Style.STROKE
                color = COLORS[ind % (COLORS.size)][1]
                strokeWidth = RECT_STROKE_WIDTH
                alpha = 200
            }
            // Draw Rect around the detectedObject detection
            frame.overlayCanvas.drawRect(
                detectedObject.boundingBox,
                boundingRectPaint
            )

            // Draw LABEL TEXT below detectedObject detection
            val textPaint = Paint().apply {
                color = COLORS[ind % (COLORS.size)][0]
                textSize = TEXT_SIZE
                alpha = 255
            }
            val textRectPaint = Paint().apply {
                style = Paint.Style.FILL_AND_STROKE
                color = COLORS[ind % (COLORS.size)][1]
                strokeWidth = RECT_STROKE_WIDTH
                alpha = 255
            }


            val labels = detectedObject.labels

            var textLineMaxWidth = // Initial width
                detectedObject.boundingBox.width() - (2 * RECT_STROKE_WIDTH)
            var yTextLineOffset = LINE_HEIGHT

            val idText = "Tracking ID: ${detectedObject.trackingId}"
            /*
            // Option 1: Text size will shrink to match the rect
            frame.overlayCanvas.drawRect(
                detectedObject.boundingBox.left.toFloat(),
                detectedObject.boundingBox.bottom.toFloat(),
                detectedObject.boundingBox.right.toFloat(),
                detectedObject.boundingBox.bottom + ((labels.size + 1) * LINE_HEIGHT) + TEXT_PADDING,
                textRectPaint
            )
            frame.overlayCanvas.drawText(
                idText,
                detectedObject.boundingBox.left + RECT_STROKE_WIDTH,
                detectedObject.boundingBox.bottom + (yTextLineOffset),
                textPaint.apply {
                    var textLineWidth = textPaint.measureText(idText)
                    if (textLineWidth > textLineMaxWidth) { // if text overflow from the rect -> shrink it
                        this.textSize *= (textLineMaxWidth / textLineWidth)
                    }
                }
            )
            yTextLineOffset += yTextLineOffset

            labels.forEachIndexed { ind, label ->
                val labelText = label.text + " | " + String.format(
                    Locale.US,
                    LABEL_FORMAT,
                    label.confidence * 100,
                    label.index
                )
                frame.overlayCanvas.drawText(
                    labelText,
                    detectedObject.boundingBox.left + RECT_STROKE_WIDTH,
                    detectedObject.boundingBox.bottom + (yTextLineOffset * (ind + 1)),
                    textPaint.apply {
                        var textLineWidth = textPaint.measureText(labelText)
                        if (textLineWidth > textLineMaxWidth) { // if text overflow from the rect -> shrink it
                            this.textSize *= (textLineMaxWidth / textLineWidth)
                        }
                    }
                )
            }
            */



            // Option 2: Inc boundingBox.width to match label text width
            textLineMaxWidth =
                max(textPaint.measureText(idText), textLineMaxWidth)
            labels.forEach { label ->
                val labelText = label.text + " | " + String.format(
                    Locale.US,
                    LABEL_FORMAT,
                    label.confidence * 100,
                    label.index
                )
                textLineMaxWidth = max(textPaint.measureText(labelText), textLineMaxWidth)
            }
            frame.overlayCanvas.drawRect(
                detectedObject.boundingBox.left.toFloat(),
                detectedObject.boundingBox.bottom.toFloat(),
                detectedObject.boundingBox.left.toFloat() + textLineMaxWidth + 2 * RECT_STROKE_WIDTH,
                detectedObject.boundingBox.bottom + ((labels.size + 1) * LINE_HEIGHT) + TEXT_PADDING,
                textRectPaint
            )
            frame.overlayCanvas.drawText(
                idText,
                detectedObject.boundingBox.left + RECT_STROKE_WIDTH,
                detectedObject.boundingBox.bottom + (yTextLineOffset),
                textPaint
            )
            yTextLineOffset += yTextLineOffset
            labels.forEachIndexed { ind, label ->
                val labelText = label.text + " " + String.format(
                    Locale.US,
                    LABEL_FORMAT,
                    label.confidence * 100,
                    label.index
                )
                frame.overlayCanvas.drawText(
                    labelText,
                    detectedObject.boundingBox.left + RECT_STROKE_WIDTH,
                    detectedObject.boundingBox.bottom + (yTextLineOffset * (ind + 1)),
                    textPaint
                )
            }

        }
    }

    companion object {
        private const val RECT_STROKE_WIDTH = 4.0f
        private const val TEXT_SIZE = 35.0f
        private const val TEXT_PADDING = 10.0f
        private const val LINE_HEIGHT = TEXT_SIZE + TEXT_PADDING
        private const val LABEL_FORMAT = "confidence: %.2f%% (index: %d)"

        //Color = [Detection IND][ID PAINT][BOX PAINT]
        private val COLORS =
            arrayOf(
                intArrayOf(Color.BLACK, Color.WHITE),
                intArrayOf(Color.WHITE, Color.MAGENTA),
                intArrayOf(Color.BLACK, Color.LTGRAY),
                intArrayOf(Color.WHITE, Color.RED),
                intArrayOf(Color.WHITE, Color.BLUE),
                intArrayOf(Color.WHITE, Color.DKGRAY),
                intArrayOf(Color.BLACK, Color.CYAN),
                intArrayOf(Color.BLACK, Color.YELLOW),
                intArrayOf(Color.WHITE, Color.BLACK),
                intArrayOf(Color.BLACK, Color.GREEN)
            )
    }
}