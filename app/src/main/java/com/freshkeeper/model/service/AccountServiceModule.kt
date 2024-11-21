package com.freshkeeper.model.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountServiceModule {
    @Binds
    @Singleton
    abstract fun bindAccountService(accountServiceImpl: AccountServiceImpl): AccountService
}
