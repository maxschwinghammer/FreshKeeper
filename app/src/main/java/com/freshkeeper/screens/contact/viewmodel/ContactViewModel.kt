package com.freshkeeper.screens.contact.viewmodel

import com.freshkeeper.screens.AppViewModel
import com.freshkeeper.service.ContactService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
    @Inject
    constructor(
        private val contactService: ContactService,
    ) : AppViewModel() {
        fun sendContactForm(
            subject: String,
            message: String,
        ) {
            launchCatching {
                contactService.sendContactEmail(subject, message)
            }
        }
    }
