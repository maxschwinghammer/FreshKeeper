package com.freshkeeper.screens.help.viewmodel

import com.freshkeeper.model.Membership
import com.freshkeeper.screens.AppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HelpViewModel
    @Inject
    constructor(
        // private val contactService: ContactService,
    ) : AppViewModel() {
        private val _membership = MutableStateFlow(Membership())
        val membership: StateFlow<Membership> = _membership.asStateFlow()

        init {
            launchCatching {
                // _membership.value = contactService.getMembershipStatus()
            }
        }
    }
