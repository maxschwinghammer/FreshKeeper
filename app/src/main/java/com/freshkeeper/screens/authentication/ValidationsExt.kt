package com.freshkeeper.screens.authentication

import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASS_LENGTH = 12
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

fun String.isValidEmail(): Boolean = this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean =
    this.isNotBlank() &&
        this.length >= MIN_PASS_LENGTH &&
        Pattern.compile(PASS_PATTERN).matcher(this).matches()
