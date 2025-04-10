package com.freshkeeper.screens.landingpage

import android.R.attr.maxWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.BackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlin.math.max
import kotlin.math.min

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story(
    onComplete: () -> Unit,
    navController: NavHostController,
) {
    val screens =
        remember {
            listOf<@Composable () -> Unit>(
                { Story0() },
                { Story1() },
                { Story2() },
                { Story3(navController) },
                { Story4() },
            )
        }

    val stepCount = screens.size
    val currentStep = remember { mutableIntStateOf(0) }
    val isPaused = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
    ) {
        val imageModifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (currentStep.intValue == stepCount - 1 && offset.x > maxWidth / 2) {
                                onComplete()
                            } else {
                                currentStep.intValue =
                                    if (offset.x < maxWidth / 2) {
                                        max(0, currentStep.intValue - 1)
                                    } else {
                                        min(stepCount - 1, currentStep.intValue + 1)
                                    }
                                isPaused.value = false
                            }
                        },
                        onPress = {
                            try {
                                isPaused.value = true
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
            onComplete = onComplete,
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
                onClick = onComplete,
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
