package com.freshkeeper.service.profile

interface ProfileService {
    fun formatMemberSince(days: Long): String
}
