package com.davidrevolt.playground.feature.camerax.detectors.facedetector

import android.graphics.Color
import android.graphics.Paint
import androidx.camera.effects.Frame
import androidx.camera.view.PreviewView
import com.davidrevolt.playground.feature.camerax.detectors.DetectorOverlayEffect
import com.google.mlkit.vision.face.Face
import java.util.Locale
import kotlin.math.max


class FaceOverlayEffect(previewView: PreviewView) :
    DetectorOverlayEffect<Face>(previewView) {

    override fun drawOnDetections(detections: List<Face>, frame: Frame) {
        detections.forEachIndexed { ind, face ->
            val boundingRectPaint = Paint().apply {
                style = Paint.Style.STROKE
                color = COLORS[ind % (COLORS.size)][1]
                strokeWidth = RECT_STROKE_WIDTH
                alpha = 200
            }
            // Draw Rect around the face detection
            frame.overlayCanvas.drawRect(
                face.boundingBox,
                boundingRectPaint
            )

            // Draw TEXT DATA below face detection
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


            var textLines = listOf(
                "ID: " + face.trackingId,
                String.format(
                    Locale.US, "Smiling: %d%%",
                    (face.smilingProbability?.times(100))?.toInt()
                ),
                //    String.format(Locale.US, "Left eye open: %.1f", face.leftEyeOpenProbability),
                //       String.format(Locale.US, "Right eye open: %.1f", face.rightEyeOpenProbability)
            )

            var textLineMaxWidth =
                face.boundingBox.width() - (2 * RECT_STROKE_WIDTH)
            var yTextLineOffset = LINE_HEIGHT

            /*
            // Option 1: Text size will shrink to match the rect
            frame.overlayCanvas.drawRect(
                face.boundingBox.left.toFloat(),
                face.boundingBox.bottom.toFloat(),
                face.boundingBox.right.toFloat(),
                face.boundingBox.bottom + (textLines.size * LINE_HEIGHT) + TEXT_PADDING,
                textRectPaint
            )
            textLines.forEachIndexed { ind, textLine ->
                frame.overlayCanvas.drawText(
                    textLine,
                    face.boundingBox.left + RECT_STROKE_WIDTH,
                    face.boundingBox.bottom + (yTextLineOffset * (ind + 1)),
                    textPaint.apply {
                        var textLineWidth = textPaint.measureText(textLine)
                        if (textLineWidth > textLineMaxWidth) { // if text overflow from the rect -> shrink it
                            this.textSize *= (textLineMaxWidth / textLineWidth)
                        }
                    }
                )
            }
            */

            // Option 2: Inc boundingBox.width to match label text width
            textLines.forEach { textLine ->
                textLineMaxWidth = max(textPaint.measureText(textLine), textLineMaxWidth)
            }
            frame.overlayCanvas.drawRect(
                face.boundingBox.left.toFloat(),
                face.boundingBox.bottom.toFloat(),
                face.boundingBox.left.toFloat() + textLineMaxWidth + 2 * RECT_STROKE_WIDTH,
                face.boundingBox.bottom + (textLines.size * LINE_HEIGHT) + TEXT_PADDING,
                textRectPaint
            )
            textLines.forEachIndexed { ind, textLine ->
                frame.overlayCanvas.drawText(
                    textLine,
                    face.boundingBox.left + RECT_STROKE_WIDTH,
                    face.boundingBox.bottom + (yTextLineOffset * (ind + 1)),
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


