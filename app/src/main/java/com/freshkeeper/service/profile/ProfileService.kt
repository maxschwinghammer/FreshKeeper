package com.freshkeeper.service.profile

import android.content.Context

interface ProfileService {
    fun formatMemberSince(
        days: Long,
        context: Context,
    ): String
}
