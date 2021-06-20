package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.MenuBinding
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil

private const val NUM_PAGES = 3

class Menu : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var menu: TabLayout
    private lateinit var fab: View
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: MenuBinding

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = AuthUtil.auth
        fb = FirebaseUtil.fireStore

        // Check if the user is signed in
        if (auth.currentUser == null) {
            Log.d("Menu", "인증 없어서 이동")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // View 들을 init
        binding.apply {
            menu = tabLayout
            menuAdapter = MenuAdapter(this@Menu)
            viewPager = pager
            viewPager.adapter = menuAdapter
            fab = floatingActionButton
        }

        TabLayoutMediator(menu, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.camp_info)
                }
                1 -> {
                    tab.text = getString(R.string.reserve)
                }
                2 -> {
                    tab.text = getString(R.string.board)
                }
            }
        }.attach()


    }

    // 생명주기 2
    // Check if the user is signed in
    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    //
    private fun logOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // FAB 눌렀을 시에
    fun showPopup(view: View) {
        val popup = PopupMenu(this, view).apply {
            setForceShowIcon(true) // 아이콘 무조건 나오게 하기 Q 이상
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    // User 용
                    R.id.reserve -> {
                        startActivity(Intent(this@Menu, ReserveListActivity::class.java))
                        true
                    }
                    R.id.user -> {
                        startActivity(Intent(this@Menu, UserInfoActivity::class.java))
                        true
                    }
                    R.id.logout -> { // 로그아웃
                        logOut()
                        true
                    }
                    // Manager 용
                    R.id.reserve_manager -> {
                        startActivity(Intent(this@Menu, ReserveListActivity::class.java))
                        true
                    }
                    R.id.add_camp -> {
                        startActivity(Intent(this@Menu, CampWriteActivity::class.java))
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
        val inflater = popup.menuInflater
        val condition = auth.currentUser?.displayName
        if (condition != null && condition == "manager") {
            inflater.inflate(R.menu.menu_setting_manager, popup.menu)
        } else {
            inflater.inflate(R.menu.menu_setting, popup.menu)
        }
        popup.show()
    }

}


class MenuAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            2 -> MenuFragmentBoard()
            1 -> MenuFragmentReserve()
            else -> MenuFragmentCampInfo()
        }
        return fragment
    }
}

