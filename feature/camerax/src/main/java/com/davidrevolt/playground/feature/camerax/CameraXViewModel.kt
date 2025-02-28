package com.davidrevolt.playground.feature.camerax

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidrevolt.playground.feature.camerax.detectors.DetectorProcessor
import com.davidrevolt.playground.feature.camerax.detectors.customobjectdetector.CustomObjectDetectorProcessor
import com.davidrevolt.playground.feature.camerax.detectors.facedetector.FaceDetectorProcessor
import com.davidrevolt.playground.feature.camerax.detectors.objectdetector.ObjectDetectorProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class CameraXViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) :
    ViewModel() {

    private var cameraController: LifecycleCameraController = LifecycleCameraController(context)
    private lateinit var cameraSelector: CameraSelector
    private lateinit var availableCameraSelectors: List<CameraSelector>

    private var detectorProcessor: DetectorProcessor<*>? = null
    private val availableDetectors = mutableListOf(
        ::ObjectDetectorProcessor,
        ::CustomObjectDetectorProcessor,
        ::FaceDetectorProcessor
    )
    private var analysisExecutor = Executors.newSingleThreadExecutor()

    private val _cameraXUiState = MutableStateFlow<CameraXUiState>(CameraXUiState.Loading)
    val cameraXUiState = _cameraXUiState.asStateFlow()


    init {
        // What to do when var cameraController finished the initialization:
        val cameraProviderFuture = LifecycleCameraController(context).initializationFuture
        cameraProviderFuture.addListener({
            try {
                cameraProviderFuture.get()
                availableCameraSelectors = listOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                ).filter { cameraSelector ->
                    cameraController.hasCamera(cameraSelector)
                }
                cameraSelector = availableCameraSelectors[0]
                cameraController.cameraSelector = cameraSelector

                // This guarantees only one image will be delivered for analysis at a time
                cameraController.imageAnalysisBackpressureStrategy =
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                _cameraXUiState.value = CameraXUiState.Ready(
                    availableDetectors.map(::getDetectorDescription)
                )
            } catch (e: Exception) {
                Log.e(TAG, "cameraX Initialization failed: ${e.message}")
                _cameraXUiState.value = CameraXUiState.Failure
            }

        }, ContextCompat.getMainExecutor(context))
    }


    // Connect PreviewView with the cameraController
    fun bindPreviewUseCase(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            previewView.controller = cameraController // Connect CameraController to preview
            cameraController.unbind()
            cameraController.bindToLifecycle(lifecycleOwner) //Because cameraController is Lifecycle aware
        }
    }


    // Setting Img Detector and EFFECT - DRAWING
    fun bindAnalysisUseCase(
        detectorIndex: Int,
        previewView: PreviewView
    ) {
        try {
            stopAnalysisUseCase()
            detectorProcessor = availableDetectors[detectorIndex].invoke(previewView)

            if (detectorProcessor?.effect?.overlayEffect != null) {
                cameraController.setEffects(
                  setOf(detectorProcessor!!.effect!!.overlayEffect)
                )
            }

            // IMG ANALYSIS
            analysisExecutor = Executors.newSingleThreadExecutor()
            cameraController.setImageAnalysisAnalyzer(
                analysisExecutor,
                MlKitAnalyzer(
                    listOf(detectorProcessor!!.detector),
                    COORDINATE_SYSTEM_VIEW_REFERENCED,
                    analysisExecutor
                ) { mlkitResult: MlKitAnalyzer.Result? ->
                    detectorProcessor!!.processMlKitAnalyzerResult(mlkitResult = mlkitResult)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "bindAnalysisUseCase failed: ${e.message}")
            _cameraXUiState.value = CameraXUiState.Failure
        }
    }


    fun stopAnalysisUseCase() {
        viewModelScope.launch {
            if (detectorProcessor != null) {
                cameraController.clearImageAnalysisAnalyzer()
                cameraController.clearEffects()
                //  analysisExecutor.shutdownNow()
                detectorProcessor!!.stop()
            }
        }

    }

    private fun getDetectorDescription(constructor: KFunction1<PreviewView, DetectorProcessor<out Any>>): String =
        when (constructor) {
            ::ObjectDetectorProcessor -> "Object Detection"
            ::CustomObjectDetectorProcessor -> "Custom Object Detection"
            ::FaceDetectorProcessor -> "Face Detection"
            else -> "Unknown Detector: ${constructor.javaClass.name}"
        }

    fun flipCamera() {
        viewModelScope.launch {
            // Toggle between front and back camera
            val newCameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            if (cameraController.hasCamera(newCameraSelector)) {
                cameraController.cameraSelector = newCameraSelector
                cameraSelector = newCameraSelector
            } else {
                Log.e(TAG, "Failed to flip camera: device doesn't have $newCameraSelector")
            }
        }
    }

    companion object {
        private const val TAG = "ImageAnalysis"
    }
}


sealed interface CameraXUiState {
    data class Ready(val availableDetectors: List<String>) :
        CameraXUiState

    data object Loading : CameraXUiState
    data object Failure : CameraXUiState

}

