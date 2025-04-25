package com.freshkeeper.service

import android.content.Context
import java.io.File

class AssetFileManager(
    private val context: Context,
) {
    fun copyCsvFromAssets(): Boolean =
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("name_category_mapping.csv")
            val outputFile = File(context.filesDir, "name_category_mapping.csv")
            if (!outputFile.exists()) {
                inputStream.use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
}
