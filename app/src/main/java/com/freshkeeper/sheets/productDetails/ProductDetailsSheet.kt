package com.freshkeeper.sheets.productDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.ProductDetails
import com.freshkeeper.service.ProductDetailsServiceImpl
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsSheet(
    sheetState: SheetState,
    editProductSheetState: SheetState,
    foodItem: FoodItem,
) {
    val coroutineScope = rememberCoroutineScope()
    val productDetails = remember { mutableStateOf<ProductDetails?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val productDetailsService = remember { ProductDetailsServiceImpl() }

    LaunchedEffect(Unit) {
        productDetails.value =
            foodItem.barcode?.let {
                productDetailsService.fetchProductDetails(it)
            }
        isLoading.value = false
    }

    ModalBottomSheet(
        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
        sheetState = sheetState,
        containerColor = ComponentBackgroundColor,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(R.string.product_details),
                color = TextColor,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier =
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
            )
            if (isLoading.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(color = AccentTurquoiseColor)
                }
            } else {
                productDetails.value?.let { details ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreyColor)
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                    ) {
                        details.brand?.let {
                            Text(
                                stringResource(R.string.brand, it),
                                color = TextColor,
                                fontSize = 14.sp,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (details.ingredients != null) {
                        Column(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreyColor)
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                        ) {
                            val firstIngredient = details.ingredients.split(".").first()
                            Column {
                                Text(
                                    stringResource(R.string.ingredients_heading),
                                    color = TextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(firstIngredient, fontSize = 14.sp, color = TextColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    details.nutriments?.let { nutriments ->
                        val certificatesAvailable =
                            details.vegan == true ||
                                details.vegetarian == true ||
                                details.organic == true
                        val nutriScoreAvailable = !details.nutriScore.isNullOrEmpty()
                        when {
                            certificatesAvailable && nutriScoreAvailable -> {
                                Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                    CertificatesSection(
                                        details,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    NutriScoreSection(
                                        score = details.nutriScore!!,
                                        modifier = Modifier.weight(1f).fillMaxHeight(),
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                NutrimentsSection(nutriments)
                            }
                            certificatesAvailable || nutriScoreAvailable -> {
                                if (nutriScoreAvailable) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        NutrimentsSection(nutriments, modifier = Modifier.weight(1f))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        details.nutriScore?.let { score ->
                                            NutriScoreSection(score, modifier = Modifier.weight(1f))
                                        }
                                    }
                                } else {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        NutrimentsSection(nutriments, modifier = Modifier.weight(1f))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        CertificatesSection(details, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            else -> {
                                NutrimentsSection(nutriments)
                            }
                        }
                    }
                } ?: Text(stringResource(R.string.no_product_details), color = TextColor)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        editProductSheetState.show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = WhiteColor),
            ) {
                Text(stringResource(R.string.back), color = ComponentBackgroundColor)
            }
        }
    }
}
