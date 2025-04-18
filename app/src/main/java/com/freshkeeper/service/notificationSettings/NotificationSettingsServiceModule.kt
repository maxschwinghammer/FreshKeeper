package com.freshkeeper.service.notificationSettings

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationSettingsServiceModule {
    @Binds
    @Singleton
    abstract fun bindNotificationSettingsService(notificationSettingsService: NotificationSettingsServiceImpl): NotificationSettingsService
}
