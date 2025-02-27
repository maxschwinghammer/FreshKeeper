package com.freshkeeper.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun NoProductsText(
    titleId: Int,
    imageId: Int,
    stringId: Int,
) {
    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(ComponentBackgroundColor)
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(15.dp))
                .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.size(25.dp),
                contentDescription = null,
                painter = painterResource(id = imageId),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = titleId),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AccentTurquoiseColor,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = stringResource(stringId))
    }
}
