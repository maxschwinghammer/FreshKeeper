package com.freshkeeper.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.inject.Inject
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ContactServiceImpl
    @Inject
    constructor(
        private val accountService: AccountService,
    ) : ContactService {
        override suspend fun sendContactEmail(
            subject: String,
            message: String,
        ) {
            val userEmail = accountService.getEmailForCurrentUser()

            if (userEmail.isEmpty()) {
                throw IllegalStateException("User email is not available")
            }

            Log.d(
                "ContactServiceImpl",
                "Sending email with subject: $subject and message: $message",
            )

            withContext(Dispatchers.IO) {
                try {
                    val properties =
                        Properties().apply {
                            put("mail.smtp.auth", "true")
                            put("mail.smtp.starttls.enable", "true")
                            put("mail.smtp.host", "smtp.strato.de")
                            put("mail.smtp.port", "587")
                        }

                    val session =
                        Session.getInstance(
                            properties,
                            object : javax.mail.Authenticator() {
                                override fun getPasswordAuthentication() =
                                    javax.mail.PasswordAuthentication(
                                        "feedback@freshkeeper.de",
                                        "4??!f_UG!H52yM@",
                                    )
                            },
                        )

                    val emailMessage =
                        MimeMessage(session).apply {
                            setFrom(InternetAddress("feedback@freshkeeper.de"))
                            addRecipient(
                                Message.RecipientType.TO,
                                InternetAddress("info@freshkeeper.de"),
                            )
                            setSubject(subject)
                            setText("Message from: $userEmail\n\n$message")
                        }

                    Transport.send(emailMessage)
                    Log.d("ContactServiceImpl", "Email sent successfully")
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw Exception("Failed to send email: ${e.message}")
                }
            }
        }
    }
