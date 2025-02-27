package com.freshkeeper.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShareServiceModule {
    @Binds
    @Singleton
    abstract fun bindShareService(shareServiceImpl: ShareServiceImpl): ShareService
}
