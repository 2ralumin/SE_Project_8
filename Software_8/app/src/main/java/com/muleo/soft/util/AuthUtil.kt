package com.muleo.soft.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muleo.soft.BuildConfig

object AuthUtil {
    /** Use emulators only in debug builds  */
    private val sUseEmulators: Boolean = BuildConfig.DEBUG

    val auth: FirebaseAuth by lazy {
        val auth = Firebase.auth
        if (sUseEmulators) {
            auth.useEmulator("10.0.2.2", 9099)
        }
        auth
    }

}