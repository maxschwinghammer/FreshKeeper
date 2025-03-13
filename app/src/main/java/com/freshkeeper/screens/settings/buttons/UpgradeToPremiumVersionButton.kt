package com.freshkeeper.screens.settings.buttons

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.freshkeeper.R
import com.freshkeeper.model.Membership
import com.freshkeeper.screens.settings.viewmodel.SettingsViewModel
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.TextColor

@Suppress("ktlint:standard:function-naming")
@Composable
fun UpgradeToPremiumVersionButton(
    membership: Membership,
    onManagePremiumClick: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current

    val billingClient =
        BillingClient
            .newBuilder(context)
            .enablePendingPurchases(
                PendingPurchasesParams
                    .newBuilder()
                    .enableOneTimeProducts()
                    .build(),
            ).setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases?.forEach { purchase ->
                        if (purchase.products.contains("premium_monthly") &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        ) {
                            val durationInDays = 30
                            viewModel.activatePremiumMembership(
                                "monthly",
                                durationInDays,
                            )
                        } else if (purchase.products.contains("premium_yearly") &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        ) {
                            val durationInDays = 365
                            viewModel.activatePremiumMembership(
                                "yearly",
                                durationInDays,
                            )
                        }
                    }
                }
            }.build()

    val productList =
        listOf(
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId("premium_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId("premium_yearly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        )

    val params =
        QueryProductDetailsParams
            .newBuilder()
            .setProductList(productList)
            .build()

    fun queryPendingPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams
                .newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                        billingClient.consumeAsync(
                            ConsumeParams
                                .newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build(),
                        ) { _, _ -> }
                    }
                }
            }
        }
    }

    billingClient.startConnection(
        object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPendingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle disconnection if needed
            }
        },
    )

    val buttonText =
        when {
            membership.hasPremium -> stringResource(R.string.manage_premium_membership)
            membership.hasTested -> stringResource(R.string.activate_premium_membership)
            else -> stringResource(R.string.test_premium_membership)
        }

    Button(
        onClick = {
            if (membership.hasPremium) {
                onManagePremiumClick()
            } else {
                viewModel.activatePremiumMembership(
                    "monthly",
                    30,
                )
                /*billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        productDetailsList.isNotEmpty()
                    ) {
                        val productDetails = productDetailsList[0]

                        val flowParams =
                            BillingFlowParams
                                .newBuilder()
                                .setProductDetailsParamsList(
                                    listOf(
                                        BillingFlowParams.ProductDetailsParams
                                            .newBuilder()
                                            .setProductDetails(productDetails)
                                            .build(),
                                    ),
                                ).build()

                        billingClient.launchBillingFlow(context as Activity, flowParams)
                    }
                }*/
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
                text = buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
            )
            Image(
                painter = painterResource(R.drawable.premium),
                contentDescription = "Premium Icon",
                modifier =
                    Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),
            )
        }
    }
}
