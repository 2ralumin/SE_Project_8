package com.muleo.soft.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muleo.soft.BuildConfig



class FirebaseUtil {
    /** Use emulators only in debug builds  */
    private val sUseEmulators: Boolean = BuildConfig.DEBUG

    private var FIRESTORE: FirebaseFirestore? = null

    // 앱 전체에서 이 메소드를 통해 Firebase를 가져옴으로서 DEBUG 모드시 항상 Firestore 에뮬레이터에 연결됩니다.
    fun getFirestore(): FirebaseFirestore? {
        if (FIRESTORE == null) {
            FIRESTORE = Firebase.firestore

            // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                FIRESTORE!!.useEmulator("10.0.2.2", 8080)
            }
        }
        return FIRESTORE
    }

}