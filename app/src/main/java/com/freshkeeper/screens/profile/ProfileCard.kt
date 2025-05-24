package com.freshkeeper.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.freshkeeper.model.ImageType
import com.freshkeeper.model.Picture
import com.freshkeeper.screens.profile.viewmodel.ProfileViewModel
import com.freshkeeper.service.PictureConverter
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileCard(
    name: String?,
    profilePicture: Picture?,
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val pictureConverter = PictureConverter()

    val memberSince =
        profileViewModel.memberSinceDays.collectAsState().value.let {
            profileViewModel.formatMemberSince(it)
        }

    Card(
        modifier =
            Modifier
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(ComponentBackgroundColor)
                    .padding(16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, ComponentStrokeColor, RoundedCornerShape(50)),
                ) {
                    profilePicture?.let {
                        when (it.type) {
                            ImageType.BASE64 -> {
                                val decodedImage =
                                    it.image?.let { it1 ->
                                        pictureConverter.convertBase64ToBitmap(it1)
                                    }
                                if (decodedImage != null) {
                                    Image(
                                        bitmap = decodedImage.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                            ImageType.URL -> {
                                Image(
                                    painter = rememberAsyncImagePainter(it.image),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            null -> {}
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name ?: "Unknown user",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = memberSince,
                        fontSize = 14.sp,
                        color = TextColor,
                    )
                }
            }
        }
    }
}
