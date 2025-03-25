@file:Suppress("ktlint:standard:import-ordering")

package com.freshkeeper.screens.landingpage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    stepCount: Int,
    stepDuration: Int,
    unSelectedColor: Color,
    selectedColor: Color,
    currentStep: Int,
    onStepChanged: (Int) -> Unit,
    isPaused: Boolean = false,
    onComplete: () -> Unit,
) {
    val currentStepState = remember(currentStep) { mutableIntStateOf(currentStep) }
    val progress = remember(currentStep) { Animatable(0f) }

    Row(
        modifier = modifier,
    ) {
        for (i in 0 until stepCount) {
            val stepProgress =
                when {
                    i == currentStepState.intValue -> progress.value
                    i > currentStepState.intValue -> 0f
                    else -> 1f
                }
            LinearProgressIndicator(
                color = selectedColor,
                backgroundColor = unSelectedColor,
                progress = stepProgress,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .height(2.dp),
            )
        }
    }

    LaunchedEffect(
        isPaused,
        currentStep,
    ) {
        if (isPaused) {
            progress.stop()
        } else {
            for (i in currentStep until stepCount) {
                progress.animateTo(
                    1f,
                    animationSpec =
                        tween(
                            durationMillis = ((1f - progress.value) * stepDuration).toInt(),
                            easing = LinearEasing,
                        ),
                )
                if (currentStepState.intValue + 1 <= stepCount - 1) {
                    progress.snapTo(0f)
                    currentStepState.intValue += 1
                    onStepChanged(currentStepState.intValue)
                } else {
                    onComplete()
                }
            }
        }
    }
}
