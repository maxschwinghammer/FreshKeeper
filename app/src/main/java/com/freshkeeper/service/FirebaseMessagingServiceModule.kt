package com.freshkeeper.service

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseMessagingServiceModule {
    @Binds
    @Singleton
    abstract fun bindFirebaseMessagingService(firebaseMessagingServiceImpl: FirebaseMessagingServiceImpl): FirebaseMessagingService
}
