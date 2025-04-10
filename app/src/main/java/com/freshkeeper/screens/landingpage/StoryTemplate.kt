package com.freshkeeper.screens.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.FreshKeeperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun StoryTemplate(
    headline: String,
    content: @Composable ColumnScope.() -> Unit,
) {
//    val listState = rememberLazyListState()

    FreshKeeperTheme {
        Scaffold { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.radialGradient(
                                    colors =
                                        listOf(
                                            AccentTurquoiseColor.copy(alpha = 0.4f),
                                            Color.Black,
                                        ),
                                    radius = 1250f,
                                ),
                        ).padding(innerPadding),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier =
                            Modifier
                                .padding(40.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = headline,
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentTurquoiseColor,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Column(content = content)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
