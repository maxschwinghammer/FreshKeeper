package com.freshkeeper.service.profile

import android.content.Context
import com.freshkeeper.R
import javax.inject.Inject

class ProfileServiceImpl
    @Inject
    constructor() : ProfileService {
        override fun formatMemberSince(
            days: Long,
            context: Context,
        ): String =
            when {
                days > 365 -> {
                    val years = days / 365
                    val remainingMonths = (days % 365) / 30
                    if (remainingMonths > 0) {
                        if (years == 1L) {
                            context.getString(
                                R.string.member_since_year_and_month_single_single,
                                years,
                                remainingMonths,
                            )
                        } else {
                            context.getString(
                                R.string.member_since_year_and_month_plural_single,
                                years,
                                remainingMonths,
                            )
                        }
                    } else {
                        if (years == 1L) {
                            context.getString(R.string.member_since_year_single, years)
                        } else {
                            context.getString(R.string.member_since_year_plural, years)
                        }
                    }
                }
                days > 30 -> {
                    val months = days / 30
                    if (months == 1L) {
                        context.getString(R.string.member_since_month_single, months)
                    } else {
                        context.getString(R.string.member_since_month_plural, months)
                    }
                }
                days == 1L -> context.getString(R.string.member_since_yesterday)
                days == 0L -> context.getString(R.string.member_since_today)
                else -> {
                    context.getString(R.string.member_since_days, days)
                }
            }
    }
