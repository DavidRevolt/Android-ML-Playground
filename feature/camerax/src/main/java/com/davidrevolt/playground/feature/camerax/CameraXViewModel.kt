package com.davidrevolt.playground.feature.camerax

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.core.Preview
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.davidrevolt.playground.feature.camerax.detectors.DetectorProcessor
import com.davidrevolt.playground.feature.camerax.detectors.facedetector.FaceDetectorProcessor
import com.davidrevolt.playground.feature.camerax.detectors.objectdetector.ObjectDetectorProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) :
    ViewModel() {

    private var cameraController: LifecycleCameraController = LifecycleCameraController(context)
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private var detectorProcessor: DetectorProcessor<*>? = null
    private val preview = Preview.Builder().build()


    private val _cameraXUiState = MutableStateFlow<CameraXUiState>(CameraXUiState.Loading)
    val cameraXUiState = _cameraXUiState.asStateFlow()


    // Setup the cameraController AFTER its finished the initialization
    init {
        // What to do when cameraController finished the initialization:
        val cameraProviderFuture = LifecycleCameraController(context).initializationFuture
        cameraProviderFuture.addListener({
            cameraController.cameraSelector = cameraSelector

            var availableCameraSelectors = listOf(
                CameraSelector.LENS_FACING_BACK,
                CameraSelector.LENS_FACING_FRONT
            ).filter { lensFacing ->
                cameraController.hasCamera(cameraLensToCameraSelector(lensFacing))
            }
            _cameraXUiState.value = CameraXUiState.Ready
            //     _cameraXUiState.value = CameraXUiState.Data(availableCameraSelectors,cameraSelector.lensFacing )
        }, ContextCompat.getMainExecutor(context))
    }


    // Connect PreviewView with the cameraController
    fun bindPreviewUseCase(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        preview.surfaceProvider = previewView.surfaceProvider
        previewView.controller = cameraController // Connect CameraController to preview
        cameraController.unbind()
        cameraController.bindToLifecycle(lifecycleOwner) //Because cameraController is Lifecycle aware
    }

    // Setting Img Detector and EFFECT - DRAWING
    fun bindAnalysisUseCase(previewView: PreviewView) {
        stopAnalysisUseCase()
        detectorProcessor = ObjectDetectorProcessor(previewView)
        if ( detectorProcessor?.effect?.overlayEffect != null){
            cameraController.setEffects(
                setOf(detectorProcessor!!.effect!!.overlayEffect)
            )
        }


        // IMG ANALYSIS
        // This guarantees only one image will be delivered for analysis at a time
        cameraController.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        val executor = context.mainExecutor
        cameraController.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(detectorProcessor!!.detector),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { mlkitResult: MlKitAnalyzer.Result? ->
                detectorProcessor!!.processMlKitAnalyzerResult(mlkitResult = mlkitResult)
            }
        )
    }
    fun stopAnalysisUseCase(){
        if (detectorProcessor != null) {
            cameraController.clearEffects()
            cameraController.clearImageAnalysisAnalyzer()
            detectorProcessor!!.stop()
        }
    }

    private fun cameraLensToCameraSelector(lensFacing: Int): CameraSelector =
        when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
            CameraSelector.LENS_FACING_BACK -> CameraSelector.DEFAULT_BACK_CAMERA
            else -> throw IllegalArgumentException("Invalid lens facing type: $lensFacing")
        }


}


sealed interface CameraXUiState {
    data class Data(val availableCameraSelectors: List<Int>, val cameraSelector: Int) :
        CameraXUiState

    data object Ready : CameraXUiState
    data object Loading : CameraXUiState
    data object Failure : CameraXUiState

}
