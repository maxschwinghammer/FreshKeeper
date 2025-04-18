package com.freshkeeper.screens.authentication.signUp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.LightGreyColor
import com.freshkeeper.ui.theme.WhiteColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun SignUpInfo() {
    val annotatedText =
        buildAnnotatedString {
            pushStyle(SpanStyle(color = WhiteColor, fontSize = 14.sp))
            append(stringResource(R.string.sign_up_info_1) + " ")

            pushStyle(SpanStyle(color = AccentTurquoiseColor))
            withLink(
                LinkAnnotation.Url(
                    url =
                        "https://github.com/maxschwinghammer/FreshKeeper/blob/master/terms-of-service.md",
                ),
            ) {
                append(stringResource(R.string.terms_of_service))
            }

            pushStyle(SpanStyle(color = WhiteColor))
            append(" " + stringResource(R.string.sign_up_info_2) + " ")

            pushStyle(SpanStyle(color = AccentTurquoiseColor))
            withLink(
                LinkAnnotation.Url(
                    url =
                        "https://github.com/maxschwinghammer/FreshKeeper/blob/master/privacy-policy.md",
                ),
            ) {
                append(stringResource(R.string.privacy_policy))
            }
        }
    Text(
        text = annotatedText,
        style =
            TextStyle(
                fontSize = 12.sp,
                color = LightGreyColor,
                textAlign = TextAlign.Center,
            ),
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}
