package com.freshkeeper.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.service.categoryReverseMap
import com.freshkeeper.service.storageLocationReverseMap
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun LeadingIcon(
    selectedId: Int,
    type: String,
) {
    val englishName =
        when (type) {
            "storageLocations" -> storageLocationReverseMap[selectedId]
            "categories" -> categoryReverseMap[selectedId]
            else -> null
        } ?: stringResource(id = selectedId)

    val iconName = englishName.lowercase(Locale.ROOT).replace(" ", "_")
    val iconResId =
        try {
            R.drawable::class.java.getDeclaredField(iconName).getInt(null)
        } catch (_: Exception) {
            null
        }

    iconResId?.let {
        Image(
            painter = painterResource(id = it),
            contentDescription = englishName,
            modifier = Modifier.size(25.dp),
        )
    }
}
