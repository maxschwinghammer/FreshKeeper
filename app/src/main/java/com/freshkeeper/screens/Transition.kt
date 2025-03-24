package com.freshkeeper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.freshkeeper.ui.theme.TransitionColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun UpperTransition() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    TransitionColor,
                                    Color.Transparent,
                                ),
                        ),
                ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LowerTransition(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    TransitionColor,
                                ),
                        ),
                ),
    )
}
