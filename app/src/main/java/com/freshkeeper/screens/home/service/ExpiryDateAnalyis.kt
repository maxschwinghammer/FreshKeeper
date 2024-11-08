package com.freshkeeper.screens.home.service

import java.time.LocalDate
import java.util.Locale

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

            val parsedExpiryDate = LocalDate.of(fullYear, month, 1)
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

fun formatDate(date: String): String {
    val parts = date.split("-", "/", ".")
    val locale = Locale.ROOT
    return when (parts.size) {
        3 -> {
            val year =
                if (parts[2].length == 2) {
                    "20${parts[2]}"
                } else {
                    parts[2]
                }
            String.format(
                locale,
                "%02d.%02d.%04d",
                parts[0].toInt(),
                parts[1].toInt(),
                year.toInt(),
            )
        }
        2 -> {
            val year =
                if (parts[1].length == 2) {
                    "20${parts[1]}"
                } else {
                    parts[1]
                }
            String.format(locale, "%02d.%02d.%04d", 31, parts[0].toInt(), year.toInt())
        }
        else -> date
    }
}
