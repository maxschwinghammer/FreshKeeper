package com.freshkeeper.model

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Icon {
    data class Vector(
        val imageVector: ImageVector,
    ) : Icon()

    data class Resource(
        val painter: Painter,
    ) : Icon()
}
