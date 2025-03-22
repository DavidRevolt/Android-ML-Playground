package com.davidrevolt.playground.feature.camerax

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch


@Composable
fun CameraXScreen(
    viewModel: CameraXViewModel = hiltViewModel()
) {
    val uiState by viewModel.cameraXUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val bindPreviewUseCase: (PreviewView, LifecycleOwner) -> Unit = viewModel::bindPreviewUseCase
    val selectDetector: (Int, PreviewView) -> Unit = viewModel::bindAnalysisUseCase
    val flipCamera = viewModel::flipCamera

    val previewView = PreviewView(context)

    var selectedDetectorIndex by remember { mutableIntStateOf(-1) }
    val lazyListState = rememberLazyListState() // Manage LazyRow scroll state for detector list
    val coroutineScope = rememberCoroutineScope() // For programmatic scrolling

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (uiState) {
            is CameraXUiState.Ready -> {
                val detectorsList = (uiState as CameraXUiState.Ready).availableDetectors
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                // CAMERAX Initial setup
                LaunchedEffect(Unit) {
                    bindPreviewUseCase(previewView, lifecycleOwner)
                    if (detectorsList.isNotEmpty()) {
                        selectedDetectorIndex = detectorsList.size / 2
                        selectDetector(selectedDetectorIndex, previewView)
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    // Flip Camera button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera_flip_android),
                            contentDescription = "Flip Camera",
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable(onClick = flipCamera)
                                .padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Space between flip button and selector
                    // Detector selector
                    LazyRow(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        itemsIndexed(detectorsList) { index, detectorName ->
                            DetectorItem(
                                name = detectorName,
                                isSelected = index == selectedDetectorIndex,
                                onClick = {
                                    selectedDetectorIndex = index
                                    selectDetector(index, previewView)
                                    coroutineScope.launch {
                                        scrollToCenterOfListItem(
                                            selectedDetectorIndex,
                                            lazyListState
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        scrollToCenterOfListItem(selectedDetectorIndex, lazyListState, false)
                    }
                }
            }

            is CameraXUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) { CircularProgressIndicator() }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) { Text("CameraX init failure, see logs for details", color = Color.White) }

            }
        }
    }
}


@Composable
fun DetectorItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max) // Adapt to content width
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 1, // Single line for simplicity
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // Yellow dot below selected item
        if (isSelected) {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.Yellow, shape = androidx.compose.foundation.shape.CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp)) // Placeholder for alignment
        }
    }
}

// Scroll to center the text of the selected item
private suspend fun scrollToCenterOfListItem(
    itemIndex: Int,
    lazyListState: LazyListState,
    animate: Boolean = true
) {
    val itemInfo =
        lazyListState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
    itemInfo?.let {
        val paddingBefore = lazyListState.layoutInfo.beforeContentPadding
        val paddingAfter = lazyListState.layoutInfo.afterContentPadding
        val totalPadding = paddingBefore + paddingAfter

        val itemCenter = it.size / 2
        val viewportWidth =
            lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
        val effectiveViewportCenter = (viewportWidth - totalPadding) / 2
        val scrollOffset = itemCenter - effectiveViewportCenter
        if (animate)
            lazyListState.animateScrollToItem(itemIndex, scrollOffset)
        else
            lazyListState.scrollToItem(itemIndex, scrollOffset)
    }
}