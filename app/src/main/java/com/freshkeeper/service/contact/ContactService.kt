package com.freshkeeper.service.contact

interface ContactService {
    suspend fun sendContactEmail(
        subject: String,
        message: String,
    )
}
