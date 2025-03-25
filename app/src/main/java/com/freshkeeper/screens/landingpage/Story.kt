package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlin.math.max
import kotlin.math.min

@Suppress("ktlint:standard:function-naming")
@Composable
fun Story(onComplete: () -> Unit) {
    val images =
        remember {
            listOf(
                R.drawable.story_0,
                R.drawable.story_1,
                R.drawable.story_2,
                R.drawable.story_3,
                R.drawable.story_4,
                R.drawable.story_5,
            )
        }

    val stepCount = images.size
    val currentStep = remember { mutableIntStateOf(0) }
    val isPaused = remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121317)),
    ) {
        val imageModifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (currentStep.intValue == stepCount - 1 && offset.x > constraints.maxWidth / 2) {
                                onComplete()
                            } else {
                                currentStep.intValue =
                                    if (offset.x < constraints.maxWidth / 2) {
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

        Image(
            painter = painterResource(id = images[currentStep.intValue]),
            contentDescription = "StoryImage",
            contentScale = ContentScale.FillWidth,
            modifier = imageModifier,
        )

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
