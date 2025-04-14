package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

@Suppress("ktlint:standard:function-naming")
@Composable
fun LandingPageScreen(navController: NavHostController) {
    val screens =
        remember {
            listOf<@Composable () -> Unit>(
                { Story0() },
                { Story1() },
                { Story2() },
                { Story3() },
                { Story4(navController) },
                { Story5() },
            )
        }

    val stepCount = screens.size
    val currentStep = remember { mutableIntStateOf(0) }
    val isPaused = remember { mutableStateOf(false) }

    val containerSize = remember { mutableStateOf(IntSize.Zero) }

    fun onComplete() {
        navController.navigate("signUp")
    }

    val totalDuration = 5_000L
    val elapsedTime = remember { mutableLongStateOf(0L) }

    LaunchedEffect(isPaused.value) {
        val startTime = System.currentTimeMillis() - elapsedTime.longValue
        if (!isPaused.value) {
            elapsedTime.longValue = System.currentTimeMillis() - startTime
        }
        delay(50)
        if (elapsedTime.longValue >= totalDuration) {
            elapsedTime.longValue = 0L
            currentStep.intValue = min(stepCount - 1, currentStep.intValue + 1)
        }
    }

    FreshKeeperTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(BackgroundColor),
        ) {
            val imageModifier =
                Modifier
                    .fillMaxSize()
                    .onSizeChanged { containerSize.value = it }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset: Offset ->
                                if (containerSize.value.width > 0) {
                                    val centerX = containerSize.value.width / 2f
                                    if (currentStep.intValue == stepCount - 1 && offset.x > centerX) {
                                        onComplete()
                                    } else {
                                        currentStep.intValue =
                                            if (offset.x < centerX) {
                                                max(0, currentStep.intValue - 1)
                                            } else {
                                                min(stepCount - 1, currentStep.intValue + 1)
                                            }
                                        elapsedTime.longValue = 0L
                                    }
                                }
                            },
                            onLongPress = {
                                isPaused.value = true
                            },
                            onPress = {
                                try {
                                    awaitRelease()
                                } finally {
                                    isPaused.value = false
                                }
                            },
                        )
                    }.padding(bottom = 75.dp)

            Box(modifier = imageModifier) {
                screens[currentStep.intValue]()
            }

            ProgressIndicator(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                stepCount = stepCount,
                stepDuration = 5_000,
                unSelectedColor = Color.LightGray,
                selectedColor = WhiteColor,
                currentStep = currentStep.intValue,
                onStepChanged = { currentStep.intValue = it },
                isPaused = isPaused.value,
                onComplete = { onComplete() },
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { onComplete() },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = ComponentBackgroundColor,
                            contentColor = AccentTurquoiseColor,
                        ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, ComponentStrokeColor),
                ) {
                    Text(stringResource(R.string.skip_introduction))
                }
            }
        }
    }
}
