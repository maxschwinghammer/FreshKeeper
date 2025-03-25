package com.freshkeeper.service.product

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductServiceModule {
    @Binds
    @Singleton
    abstract fun bindProductService(productServiceImpl: ProductServiceImpl): ProductService
}
