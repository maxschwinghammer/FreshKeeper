package com.freshkeeper.screens.home

import java.time.LocalDate
import java.time.ZoneOffset

fun getMaxDaysOfMonth(
    month: Int,
    year: Int,
): Int = LocalDate.of(year, month, 1).lengthOfMonth()

fun isValidDate(date: String): Boolean {
    val parts = date.split("-", "/", ".")
    return when (parts.size) {
        3 -> {
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val fullYear = if (year < 100) year + 2000 else year

            val parsedExpiryDate = LocalDate.of(fullYear, month, day)
            val currentDate = LocalDate.now()
            val tenYearsLater = currentDate.plusYears(10)

            (parsedExpiryDate.isAfter(currentDate) || parsedExpiryDate.isEqual(currentDate)) &&
                (
                    parsedExpiryDate.isBefore(tenYearsLater) ||
                        parsedExpiryDate.isEqual(tenYearsLater)
                )
        }
        2 -> {
            val month = parts[0].toInt()
            val year = parts[1].toInt()
            val fullYear = if (year < 100) year + 2000 else year

            val maxDays = getMaxDaysOfMonth(month, fullYear)
            val parsedExpiryDate = LocalDate.of(fullYear, month, maxDays)
            val currentDate = LocalDate.now()
            val tenYearsLater = currentDate.plusYears(10)

            (parsedExpiryDate.isAfter(currentDate) || parsedExpiryDate.isEqual(currentDate)) &&
                (
                    parsedExpiryDate.isBefore(tenYearsLater) ||
                        parsedExpiryDate
                            .isEqual(tenYearsLater)
                )
        }
        else -> false
    }
}

fun convertToUnixTimestamp(date: String): Long {
    val parts = date.split("-", "/", ".")
    return when (parts.size) {
        3 -> {
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val fullYear = if (year < 100) year + 2000 else year

            val parsedExpiryDate = LocalDate.of(fullYear, month, day)
            parsedExpiryDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        }
        2 -> {
            val month = parts[0].toInt()
            val year = parts[1].toInt()
            val fullYear = if (year < 100) year + 2000 else year

            val maxDays = getMaxDaysOfMonth(month, fullYear)
            val parsedExpiryDate = LocalDate.of(fullYear, month, maxDays)
            parsedExpiryDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        }
        else -> 0L
    }
}
