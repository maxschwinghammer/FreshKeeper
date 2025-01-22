package com.freshkeeper.screens.settings.viewmodel

import com.freshkeeper.model.Membership
import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.MembershipService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val membershipService: MembershipService,
    ) : AppViewModel() {
        private val _membership = MutableStateFlow(Membership())
        val membership: StateFlow<Membership> = _membership.asStateFlow()

        init {
            launchCatching {
                _membership.value = membershipService.getMembershipStatus()
            }
        }
    }
