package com.freshkeeper.sheets

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodRecognitionSheet(
    sheetState: SheetState,
    onFoodRecognized: (Any?) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var recognizedFood by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.scan_food_item),
            fontSize = 18.sp,
            color = TextColor,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier =
                Modifier
                    .height(530.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(4.dp, ComponentStrokeColor, RoundedCornerShape(15.dp))
                    .clip(RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    val previewView = PreviewView(context)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview =
                            Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                        val imageAnalysis =
                            ImageAnalysis.Builder().build().also { analysis ->
                                analysis.setAnalyzer(
                                    ContextCompat.getMainExecutor(context),
                                ) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val inputImage =
                                            InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees,
                                            )
                                        val labeler =
                                            ImageLabeling.getClient(
                                                ImageLabelerOptions.DEFAULT_OPTIONS,
                                            )
                                        labeler
                                            .process(inputImage)
                                            .addOnSuccessListener { labels ->
                                                if (labels.isNotEmpty()) {
                                                    val bestLabel =
                                                        labels.maxByOrNull {
                                                            it.confidence
                                                        }
                                                    bestLabel?.text?.let {
                                                        recognizedFood = it
                                                        onFoodRecognized(it)
                                                        coroutineScope.launch {
                                                            manualInputSheetState.show()
                                                            sheetState.hide()
                                                        }
                                                    }
                                                }
                                            }.addOnFailureListener { e ->
                                                e.printStackTrace()
                                            }.addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis,
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))

                    previewView
                },
            )
            DisposableEffect(Unit) {
                onDispose {
                    ProcessCameraProvider
                        .getInstance(context)
                        .get()
                        ?.unbindAll()
                }
            }
        }
    }
}
