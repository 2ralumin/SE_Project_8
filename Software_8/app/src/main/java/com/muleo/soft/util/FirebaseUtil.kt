package com.muleo.soft.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muleo.soft.BuildConfig


object FirebaseUtil {
    /** Use emulators only in debug builds  */
    private val sUseEmulators: Boolean = BuildConfig.DEBUG

    val fireStore: FirebaseFirestore by lazy {
        val fb = Firebase.firestore
        if (sUseEmulators) {
            fb.useEmulator("10.0.2.2", 8080)
        }
        fb
    }
}