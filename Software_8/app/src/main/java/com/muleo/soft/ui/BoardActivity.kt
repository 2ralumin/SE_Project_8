package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivityBoardBinding
import com.muleo.soft.entity.PostType
import com.muleo.soft.util.AuthUtil

class BoardActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    // View
    private lateinit var binding: ActivityBoardBinding
    private lateinit var navController: NavController

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = AuthUtil.auth

        // Check if the user is signed in
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        val postType = intent.getIntExtra("PostType", PostType.REVIEW.ordinal)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_board_activity) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(
            R.navigation.nav_board,
            bundleOf("postType" to postType, "title" to PostType.values()[postType].s)
        )

        // 툴바 설정
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = {
                if (supportParentActivityIntent == null) {
                    if (!navController.popBackStack()) finish()
                    return@AppBarConfiguration true
                } else {
                    return@AppBarConfiguration onSupportNavigateUp()
                }
            }
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

    }

    // 생명주기 2
    // Check if the user is signed in
    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        if (!navController.popBackStack()) super.onBackPressed()
    }
}