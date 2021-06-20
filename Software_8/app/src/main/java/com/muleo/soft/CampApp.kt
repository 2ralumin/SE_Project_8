package com.muleo.soft

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.muleo.soft.entity.CampRepository
import com.muleo.soft.entity.User
import com.muleo.soft.entity.UserRepository
import com.muleo.soft.util.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// 	com.muleo.soft
// 	market://details?id=com.muleo.soft
//  Mj99rZMzJYjAg77Kl4Dggc+Expg=

// 네이티브 앱키 ef25222e3db58594e84ab9de64c278ab

class CampApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { Database.getDatabase(this, applicationScope) }
    val userRep by lazy { UserRepository(database.userDao()) }
    val campRep by lazy { CampRepository(database.campDao()) }

    val fb by lazy { FirebaseUtil() }

    var isUser = true
    lateinit var user: User

    override fun onCreate() {
        super.onCreate()
        //getHashKey()
    }

    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }

}