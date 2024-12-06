package com.freshkeeper.screens.authentication

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.navigation.NavController
import com.freshkeeper.ERROR_TAG
import com.freshkeeper.UNEXPECTED_CREDENTIAL
import com.freshkeeper.model.service.AccountService
import com.freshkeeper.screens.AppViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : AppViewModel() {
        fun onSignInWithGoogle(
            credential: Credential,
            navController: NavController,
        ) {
            launchCatching {
                if (credential is CustomCredential &&
                    credential.type ==
                    TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(
                            credential.data,
                        )
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    navController.navigate("home") { launchSingleTop = true }
                } else {
                    Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                }
            }
        }
    }
