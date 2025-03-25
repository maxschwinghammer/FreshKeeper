package com.freshkeeper.service.membership

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MembershipServiceModule {
    @Binds
    @Singleton
    abstract fun bindMembershipService(membershipServiceImpl: MembershipServiceImpl): MembershipService
}
