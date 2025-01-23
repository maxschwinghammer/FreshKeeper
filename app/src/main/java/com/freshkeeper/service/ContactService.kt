package com.freshkeeper.service

interface ContactService {
    suspend fun sendContactEmail(
        subject: String,
        message: String,
    )
}
