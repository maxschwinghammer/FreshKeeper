package com.freshkeeper.sheets.productDetails.viewmodel

import com.freshkeeper.model.ProductDetails
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.productDetails.ProductDetailsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel
    @Inject
    constructor(
        private val productDetailsService: ProductDetailsService,
    ) : AppViewModel() {
        suspend fun fetchProductDetails(barcode: String): ProductDetails? =
            productDetailsService
                .fetchProductDetails(barcode)
    }
