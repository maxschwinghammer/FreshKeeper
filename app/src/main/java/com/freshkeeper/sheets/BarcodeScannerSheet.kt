package com.freshkeeper.sheets

import androidx.annotation.OptIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.freshkeeper.screens.home.service.formatDate
import com.freshkeeper.screens.home.service.isValidDate
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalGetImage::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun BarcodeScannerSheet(
    sheetState: SheetState,
    onBarcodeScanned: (String, String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isBarcodeScanned by remember { mutableStateOf(false) }
    val isExpiryDateScanned by remember { mutableStateOf(false) }
    var scannedBarcode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
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
            text = if (isBarcodeScanned) "Scan expiry date" else "Scan Barcode",
            fontSize = 18.sp,
            color = TextColor,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isBarcodeScanned && !isExpiryDateScanned) {
            Button(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, AccentGreenColor, RoundedCornerShape(10.dp)),
                onClick = {
                    onBarcodeScanned(scannedBarcode, "")
                    coroutineScope.launch {
                        manualInputSheetState.show()
                        sheetState.hide()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            ) {
                Text(
                    text = "Skip scan",
                    fontSize = 18.sp,
                    color = TextColor,
                )
            }
        }

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
                                    Barcode.FORMAT_QR_CODE,
                                ).build()

                        val barcodeImageAnalysis =
                            ImageAnalysis.Builder().build().also { it ->
                                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val inputImage =
                                            InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees,
                                            )
                                        val scanner =
                                            BarcodeScanning.getClient(barcodeScannerOptions)
                                        scanner
                                            .process(inputImage)
                                            .addOnSuccessListener { barcodes ->
                                                for (barcode in barcodes) {
                                                    val rawValue = barcode.rawValue
                                                    rawValue?.let {
                                                        scannedBarcode = it
                                                        isBarcodeScanned = true
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

                        val textRecognizer =
                            TextRecognition.getClient(TextRecognizerOptions.Builder().build())
                        val textImageAnalysis =
                            ImageAnalysis.Builder().build().also {
                                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null && isBarcodeScanned) {
                                        val inputImage =
                                            InputImage
                                                .fromMediaImage(
                                                    mediaImage,
                                                    imageProxy.imageInfo.rotationDegrees,
                                                )
                                        textRecognizer
                                            .process(inputImage)
                                            .addOnSuccessListener { visionText ->
                                                if (visionText.text.isNotEmpty()) {
                                                    coroutineScope.launch {
                                                        var isFullDateFound = false

                                                        val timeoutMillis = 3000L
                                                        val startTime = System.currentTimeMillis()

                                                        val fullMhdRegex =
                                                            """\b(0?[1-9]|[12]\d|3[01])[-/.](0?[1-9]|1[0-2])[-/.](20\d{2}|[2-9]\d)\b"""
                                                                .toRegex()

                                                        val monthYearRegex =
                                                            """\b(0?[1-9]|1[0-2])[-/.](20\d{2}|[2-9]\d)\b""".toRegex()

                                                        while (!isFullDateFound &&
                                                            (System.currentTimeMillis() - startTime < timeoutMillis)
                                                        ) {
                                                            val matches = fullMhdRegex.findAll(visionText.text)
                                                            if (matches.any()) {
                                                                for (match in matches) {
                                                                    if (isValidDate(match.value)) {
                                                                        isFullDateFound = true
                                                                        expiryDate = formatDate(match.value)
                                                                        onBarcodeScanned(scannedBarcode, expiryDate)
                                                                        coroutineScope.launch {
                                                                            manualInputSheetState.show()
                                                                            sheetState.hide()
                                                                        }
                                                                        break
                                                                    }
                                                                }
                                                            }

                                                            delay(500L)
                                                        }

                                                        if (!isFullDateFound) {
                                                            val monthYearMatches = monthYearRegex.findAll(visionText.text)
                                                            for (match in monthYearMatches) {
                                                                if (isValidDate(match.value)) {
                                                                    expiryDate = formatDate(match.value)
                                                                    onBarcodeScanned(scannedBarcode, expiryDate)
                                                                    coroutineScope.launch {
                                                                        manualInputSheetState.show()
                                                                        sheetState.hide()
                                                                    }
                                                                    break
                                                                }
                                                            }
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
                                barcodeImageAnalysis,
                                textImageAnalysis,
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))

                    previewView
                },
            )
        }
    }
}
