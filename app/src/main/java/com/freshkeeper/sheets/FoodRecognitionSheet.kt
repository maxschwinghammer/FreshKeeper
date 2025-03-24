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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import com.freshkeeper.service.FoodClassifier
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodRecognitionSheet(
    sheetState: SheetState,
    onFoodRecognized: (String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var recognizedFood by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val foodClassifier = remember { FoodClassifier(context) }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
            }
        },
        sheetState = sheetState,
        containerColor = ComponentBackgroundColor,
    ) {
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
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview =
                                Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                            val imageAnalysis =
                                ImageAnalysis
                                    .Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()

                            imageAnalysis.setAnalyzer(
                                ContextCompat.getMainExecutor(ctx),
                            ) { imageProxy ->
                                val (result, confidence) = foodClassifier.classifyWithConfidence(imageProxy)
                                imageProxy.close()

                                if (confidence > 0.15f && result != recognizedFood) {
                                    recognizedFood = result
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        onFoodRecognized(result)
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
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                )
                DisposableEffect(Unit) {
                    onDispose {
                        ProcessCameraProvider.getInstance(context).get()?.unbindAll()
                        foodClassifier.close()
                    }
                }
            }
        }
    }
}
