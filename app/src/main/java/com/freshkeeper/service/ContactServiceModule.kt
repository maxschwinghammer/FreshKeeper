package com.freshkeeper.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactServiceModule {
    @Binds
    @Singleton
    abstract fun bindContactService(contactServiceImpl: ContactServiceImpl): ContactService
}
