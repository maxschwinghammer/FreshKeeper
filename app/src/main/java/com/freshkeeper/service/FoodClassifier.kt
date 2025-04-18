package com.freshkeeper.service

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.core.graphics.get
import androidx.core.graphics.scale
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class FoodClassifier(
    context: Context,
) {
    private var interpreter: Interpreter

    private val inputImageWidth = 224
    private val inputImageHeight = 224

    private val labels: List<String> =
        try {
            FileUtil.loadLabels(context, "labels.txt")
        } catch (_: Exception) {
            emptyList()
        }

    init {
        val model = FileUtil.loadMappedFile(context, "food101_model.tflite")
        interpreter = Interpreter(model)
    }

    fun classifyWithConfidence(imageProxy: ImageProxy): Pair<String, Float> {
        val bitmap = imageProxy.toBitmap()
        val resizedBitmap = bitmap.scale(inputImageWidth, inputImageHeight, false)
        val inputBuffer = convertBitmapToFloatBuffer(resizedBitmap)

        val outputArray = Array(1) { FloatArray(labels.size.takeIf { it > 0 } ?: 101) }
        interpreter.run(inputBuffer, outputArray)

        val maxIdx = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1
        val confidence = if (maxIdx >= 0) outputArray[0][maxIdx] else 0f

        return if (labels.isNotEmpty() && maxIdx in labels.indices) {
            Pair(labels[maxIdx], confidence)
        } else {
            Pair("Klasse $maxIdx", confidence)
        }
    }

    fun close() {
        interpreter.close()
    }

    private fun convertBitmapToFloatBuffer(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val input = Array(1) { Array(bitmap.height) { Array(bitmap.width) { FloatArray(3) } } }
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap[x, y]
                input[0][y][x][0] = ((pixel shr 16) and 0xFF) / 255.0f
                input[0][y][x][1] = ((pixel shr 8) and 0xFF) / 255.0f
                input[0][y][x][2] = (pixel and 0xFF) / 255.0f
            }
        }
        return input
    }
}
