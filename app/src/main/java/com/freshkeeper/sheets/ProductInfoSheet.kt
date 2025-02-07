package com.freshkeeper.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.ProductDetails
import com.freshkeeper.service.ProductDetailsServiceImpl
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfoSheet(
    sheetState: SheetState,
    editProductSheetState: SheetState,
    foodItem: FoodItem,
) {
    val coroutineScope = rememberCoroutineScope()
    val productDetails = remember { mutableStateOf<ProductDetails?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val productDetailsService = remember { ProductDetailsServiceImpl() }

    LaunchedEffect(Unit) {
        productDetails.value = productDetailsService.fetchProductDetails("5449000131805")
        isLoading.value = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.product_details),
            color = TextColor,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            productDetails.value?.let { pd ->
                pd.brand?.let { Text(stringResource(R.string.brand, it), color = TextColor) }
                pd.nutriScore?.let { score ->
                    val imageId =
                        when (score.uppercase()) {
                            "A" -> R.drawable.nutri_score_a
                            "B" -> R.drawable.nutri_score_b
                            "C" -> R.drawable.nutri_score_c
                            "D" -> R.drawable.nutri_score_d
                            "E" -> R.drawable.nutri_score_e
                            else -> null
                        }
                    imageId?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = stringResource(R.string.nutri_score_desc, score),
                            modifier = Modifier.size(50.dp),
                        )
                    }
                }
                pd.ingredients?.let {
                    Column {
                        Text(
                            stringResource(R.string.ingredients_heading),
                            color = TextColor,
                            fontSize = 16.sp,
                        )
                        Text(it, color = TextColor)
                    }
                }
                pd.labels?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        stringResource(R.string.labels_desc, it.joinToString(", ")),
                        color = TextColor,
                    )
                }
                Row {
                    pd.isVegan?.takeIf { it }?.let {
                        Image(
                            painter = painterResource(R.drawable.vegan),
                            contentDescription = stringResource(R.string.vegan),
                            modifier = Modifier.size(40.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    pd.isVegetarian?.takeIf { it }?.let {
                        Image(
                            painter = painterResource(R.drawable.vegetarian),
                            contentDescription = stringResource(R.string.vegetarian),
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
                pd.isOrganic?.let {
                    Text(
                        stringResource(
                            R.string.organic_desc,
                            if (it) {
                                stringResource(R.string.yes)
                            } else {
                                stringResource(R.string.no)
                            },
                        ),
                        color = TextColor,
                    )
                }
                pd.nutriments?.let { n ->
                    Column {
                        Text(
                            stringResource(R.string.nutrition_heading),
                            color = TextColor,
                            fontSize = 16.sp,
                        )
                        n.energyKcal?.let {
                            Text(
                                stringResource(R.string.energy, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.fat?.let {
                            Text(
                                stringResource(R.string.fat, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.carbohydrates?.let {
                            Text(
                                stringResource(R.string.carbohydrates, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.sugars?.let {
                            Text(
                                stringResource(R.string.sugars, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.fiber?.let {
                            Text(
                                stringResource(R.string.fiber, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.proteins?.let {
                            Text(
                                stringResource(R.string.proteins, it.toString()),
                                color = TextColor,
                            )
                        }
                        n.salt?.let {
                            Text(
                                stringResource(R.string.salt, it.toString()),
                                color = TextColor,
                            )
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
