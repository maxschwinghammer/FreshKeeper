package com.freshkeeper.sheets.productDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.freshkeeper.R
import com.freshkeeper.model.ProductDetails
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun CertificatesSection(
    details: ProductDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(GreyColor)
                .border(
                    1.dp,
                    ComponentStrokeColor,
                    RoundedCornerShape(10.dp),
                ).padding(10.dp),
    ) {
        Text(
            stringResource(R.string.certificates_heading),
            color = TextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            details.vegan?.takeIf { it }?.let {
                Image(
                    painter =
                        painterResource(
                            R.drawable.vegan,
                        ),
                    contentDescription =
                        stringResource(
                            R.string.vegan,
                        ),
                    modifier = Modifier.size(60.dp),
                )
            } ?: details.vegetarian?.takeIf { it }?.let {
                Image(
                    painter =
                        painterResource(
                            R.drawable.vegetarian,
                        ),
                    contentDescription =
                        stringResource(
                            R.string.vegetarian,
                        ),
                    modifier = Modifier.size(60.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            details.organic?.takeIf { it }?.let {
                Image(
                    painter = painterResource(R.drawable.bio),
                    contentDescription = "Bio",
                    modifier = Modifier.size(60.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
