package com.freshkeeper.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDetailsServiceModule {
    @Binds
    @Singleton
    abstract fun bindProductDetailsService(productDetailsServiceImpl: ProductDetailsServiceImpl): ProductDetailsService
}
