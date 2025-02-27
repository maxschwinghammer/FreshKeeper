package com.freshkeeper.screens.profileSettings.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun AccountCenterCard(
    title: String,
    icon: Any?,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onCardClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(ComponentBackgroundColor)
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) { Text(title, color = TextColor) }
            if (icon != null) {
                when (icon) {
                    is ImageVector ->
                        Icon(
                            imageVector = icon,
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    is Painter ->
                        Icon(
                            painter = icon,
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    else -> throw IllegalArgumentException("Unsupported icon type")
                }
            }
        }
    }
}

fun Modifier.card(): Modifier = this.padding(16.dp, 0.dp, 16.dp, 0.dp)
