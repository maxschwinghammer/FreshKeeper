package com.freshkeeper.sheets

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.freshkeeper.R
import com.freshkeeper.screens.home.convertToUnixTimestamp
import com.freshkeeper.screens.home.isValidDate
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun BarcodeScannerSheet(
    sheetState: SheetState,
    onBarcodeScanned: (String, Long) -> Unit,
) {
//    var isFlashOn by remember { mutableStateOf(false) }
//    var camera: Camera? by remember { mutableStateOf(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
//    val cameraPermissionRequestCode = 101
    var isBarcodeScanned by remember { mutableStateOf(false) }
    val isExpiryDateScanned by remember { mutableStateOf(false) }
    var scannedBarcode by remember { mutableStateOf("") }
    var expiryTimestamp by remember { mutableLongStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val imageAnalysisRef = remember { mutableStateOf<ImageAnalysis?>(null) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraProvider = remember { ProcessCameraProvider.getInstance(context) }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
//                camera?.cameraControl?.enableTorch(!isFlashOn)
                cameraProvider.get().unbindAll()
                if (sheetState.isVisible) {
                    sheetState.hide()
                }
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
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
            Text(
                text =
                    if (isBarcodeScanned) {
                        stringResource(R.string.scan_expiry_date)
                    } else {
                        stringResource(R.string.scan_barcode)
                    },
                color = TextColor,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.weight(1f).padding(start = 38.dp),
                textAlign = TextAlign.Center,
            )

//                Box(
//                    modifier =
//                        Modifier
//                            .padding(end = 16.dp)
//                            .clip(RoundedCornerShape(10.dp))
//                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
//                            .clickable {
//                                isFlashOn = !isFlashOn
//                                camera?.cameraControl?.enableTorch(isFlashOn)
//                            },
//                ) {
//                    Image(
//                        painter =
//                            painterResource(
//                                if (isFlashOn) {
//                                    R.drawable.flash_on
//                                } else {
//                                    R.drawable.flash_off
//                                },
//                            ),
//                        contentDescription = "Toggle flash",
//                        modifier =
//                            Modifier
//                                .size(30.dp)
//                                .padding(8.dp),
//                    )
//                }
//            }

            Box(
                modifier =
                    Modifier
                        .height(530.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(
                            4.dp,
                            if (isBarcodeScanned) {
                                AccentGreenColor
                            } else {
                                ComponentStrokeColor
                            },
                            RoundedCornerShape(15.dp),
                        ).clip(RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                val context = LocalContext.current
                val hasPermission =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
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

                                val barcodeScannerOptions =
                                    BarcodeScannerOptions
                                        .Builder()
                                        .setBarcodeFormats(
                                            Barcode.FORMAT_EAN_13,
                                            Barcode.FORMAT_EAN_8,
                                            Barcode.FORMAT_UPC_A,
                                            Barcode.FORMAT_UPC_E,
                                            Barcode.FORMAT_CODE_39,
                                            Barcode.FORMAT_CODE_128,
                                        ).build()

//                            val cameraSelector =
//                                CameraSelector
//                                    .Builder()
//                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                                    .build()
//
//                            camera =
//                                cameraProvider.bindToLifecycle(
//                                    lifecycleOwner,
//                                    cameraSelector,
//                                    preview,
//                                )

                                val imageAnalysis =
                                    ImageAnalysis
                                        .Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .setImageQueueDepth(1)
                                        .build()
                                        .also { it ->
                                            it.setAnalyzer(cameraExecutor) { imageProxy ->
                                                val mediaImage = imageProxy.image
                                                if (mediaImage != null) {
                                                    val inputImage =
                                                        InputImage.fromMediaImage(
                                                            mediaImage,
                                                            imageProxy.imageInfo.rotationDegrees,
                                                        )
                                                    if (!isBarcodeScanned) {
                                                        val scanner =
                                                            BarcodeScanning.getClient(barcodeScannerOptions)
                                                        scanner
                                                            .process(inputImage)
                                                            .addOnSuccessListener(cameraExecutor) { barcodes ->
                                                                for (barcode in barcodes) {
                                                                    val rawValue = barcode.rawValue
                                                                    rawValue?.let {
                                                                        scannedBarcode = it
                                                                        isBarcodeScanned = true
                                                                    }
                                                                }
                                                            }.addOnFailureListener { e ->
                                                                e.printStackTrace()
                                                            }.addOnCompleteListener(cameraExecutor) {
                                                                imageProxy.close()
                                                            }
                                                    } else {
                                                        val textRecognizer =
                                                            TextRecognition.getClient(
                                                                TextRecognizerOptions.DEFAULT_OPTIONS,
                                                            )
                                                        textRecognizer
                                                            .process(inputImage)
                                                            .addOnSuccessListener { visionText ->
                                                                if (visionText.text.isNotEmpty()) {
                                                                    val fullMhdRegex =
                                                                        """\b(0?[1-9]|[12]\d|3[01])[-/.](0?[1-9]|1[0-2])[-/.](20\d{2}|[2-9]\d)\b"""
                                                                            .toRegex()
                                                                    val monthYearRegex =
                                                                        """\b(0?[1-9]|1[0-2])[-/.](20\d{2}|[2-9]\d)\b"""
                                                                            .toRegex()

                                                                    val fullMatches = fullMhdRegex.findAll(visionText.text)
                                                                    for (match in fullMatches) {
                                                                        if (isValidDate(match.value)) {
                                                                            expiryTimestamp = convertToUnixTimestamp(match.value)
                                                                            onBarcodeScanned(scannedBarcode, expiryTimestamp)
                                                                            coroutineScope.launch {
                                                                                manualInputSheetState.show()
                                                                                sheetState.hide()
                                                                            }
                                                                            return@addOnSuccessListener
                                                                        }
                                                                    }

                                                                    val monthYearMatches = monthYearRegex.findAll(visionText.text)
                                                                    for (match in monthYearMatches) {
                                                                        if (isValidDate(match.value)) {
                                                                            expiryTimestamp = convertToUnixTimestamp(match.value)
                                                                            onBarcodeScanned(scannedBarcode, expiryTimestamp)
                                                                            coroutineScope.launch {
                                                                                manualInputSheetState.show()
                                                                                sheetState.hide()
                                                                            }
                                                                            return@addOnSuccessListener
                                                                        }
                                                                    }
                                                                }
                                                            }.addOnFailureListener { e ->
                                                                e.printStackTrace()
                                                            }.addOnCompleteListener {
                                                                imageProxy.close()
                                                            }
                                                    }
                                                }
                                            }
                                        }

                                imageAnalysisRef.value = imageAnalysis
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
                } else {
                    val cameraPermissionLauncher =
                        rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission(),
                            onResult = {
                                Log.d("MainActivity", "Camera permission granted: $it")
                            },
                        )
                    Button(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier =
                            Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
                    ) {
                        Text(stringResource(R.string.allow_camera), color = ComponentBackgroundColor)
                    }
                }
                if (isBarcodeScanned && !isExpiryDateScanned) {
                    Button(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp),
                        onClick = {
                            imageAnalysisRef.value?.clearAnalyzer()
                            onBarcodeScanned(scannedBarcode, 0L)
                            coroutineScope.launch {
                                manualInputSheetState.show()
                                sheetState.hide()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComponentBackgroundColor),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(
                            text = stringResource(R.string.skip_scan),
                            fontSize = 16.sp,
                            color = TextColor,
                        )
                    }
                }
            }
        }
    }
}
