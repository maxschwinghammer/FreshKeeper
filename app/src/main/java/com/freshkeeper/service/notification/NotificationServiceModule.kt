package com.freshkeeper.service.notification

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationServiceModule {
    @Binds
    @Singleton
    abstract fun bindNotificationService(notificationServiceImpl: NotificationServiceImpl): NotificationService
}
