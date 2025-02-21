package com.freshkeeper.screens.settings.buttons

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory

@Suppress("ktlint:standard:function-naming")
@Composable
fun ReviewAppButton() {
    val context = LocalContext.current
    val packageName = context.packageName
    val url = "https://play.google.com/store/apps/details?id=$packageName"

    val activity = context as? Activity
    var reviewInfo: ReviewInfo? = null

    val manager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            reviewInfo = task.result
        }
    }

    Button(
        onClick = {
            if (activity != null && reviewInfo != null) {
                val flow = manager.launchReviewFlow(activity, reviewInfo!!)
                flow.addOnCompleteListener { _ -> }
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = ComponentBackgroundColor,
                contentColor = TextColor,
            ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ComponentStrokeColor),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.rate_us),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
            )
            Image(
                painter = painterResource(R.drawable.star),
                contentDescription = "Star Icon",
                modifier =
                    Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),
            )
        }
    }
}
