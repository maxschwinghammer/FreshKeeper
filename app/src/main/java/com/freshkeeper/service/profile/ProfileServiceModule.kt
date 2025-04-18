package com.freshkeeper.service.profile

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileServiceModule {
    @Binds
    @Singleton
    abstract fun bindProfileService(profileServiceImpl: ProfileServiceImpl): ProfileService
}
