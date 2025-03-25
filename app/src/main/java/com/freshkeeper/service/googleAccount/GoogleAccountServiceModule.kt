package com.freshkeeper.service.googleAccount

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleAccountServiceModule {
    @Binds
    @Singleton
    abstract fun bindGoogleAccountService(googleAccountServiceImpl: GoogleAccountServiceImpl): GoogleAccountService
}
