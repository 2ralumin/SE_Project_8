package com.muleo.soft.util

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.muleo.soft.BuildConfig

object StorageUtil {
    /** Use emulators only in debug builds  */
    private val sUseEmulators: Boolean = BuildConfig.DEBUG

    val fireStorage: FirebaseStorage by lazy {
        val fs = Firebase.storage
        if (sUseEmulators) {
            fs.useEmulator("10.0.2.2", 9199)
        }
        fs
    }
}