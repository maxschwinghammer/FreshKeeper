package com.freshkeeper.service.inventory

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InventoryServiceModule {
    @Binds
    @Singleton
    abstract fun bindInventoryService(inventoryServiceImpl: InventoryServiceImpl): InventoryService
}
