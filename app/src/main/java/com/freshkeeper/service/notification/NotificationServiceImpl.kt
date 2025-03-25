package com.freshkeeper.service.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NotificationServiceImpl
    @Inject
    constructor() : NotificationService {
        private val firestore = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
    }
