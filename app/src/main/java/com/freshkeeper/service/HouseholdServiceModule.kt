package com.freshkeeper.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HouseholdServiceModule {
    @Binds
    @Singleton
    abstract fun bindHouseholdService(householdServiceImpl: HouseholdServiceImpl): HouseholdService
}
