package com.muleo.soft.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.muleo.soft.CampApp
import com.muleo.soft.R
import com.muleo.soft.control.CampControl
import com.muleo.soft.control.CampViewModelFactory
import com.muleo.soft.databinding.UsermenuBinding

private const val NUM_PAGES = 3

class Menu : AppCompatActivity() {

    private val cc: CampControl by viewModels {
        CampViewModelFactory((application as CampApp).campRep)
    }

    private lateinit var menu: TabLayout
    private lateinit var fab: View
    private lateinit var binding: UsermenuBinding
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UsermenuBinding.inflate(layoutInflater)
        binding.apply {
            menu = tabLayout
            menuAdapter = MenuAdapter(this@Menu)
            viewPager = pager
            viewPager.adapter = menuAdapter
            fab = floatingActionButton
            fab.setOnClickListener {
                //TODO 유저 정보 수정
            }
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

        //TODO 유저인지 아닌지는 다음과 같은 방식으로 얻어온다. -> 유저와 관리자의 구분 구현해야함
//        findViewById<TextView>(R.id.textView).apply {
//            text = if ((application as CampApp).isUser) "User" else "Manager"
//        }


        // 액티비티 시작시 네트워크 요청을 캠핑장 정보를 얻어온다
        cc.getCampByIdWithNet(20) // TODO 일단은 20 번만 얻어오는 것으로 한다.

        setContentView(binding.root)
    }

}

class MenuAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            2 -> FragmentBoard()
            1 -> FragmentReserve()
            else -> FragmentCampInfo()
        }
        fragment.arguments = Bundle().apply {
            when (position) {
                2 -> {
//                    putInt()
                }
                1 -> {

                }
                else -> {

                }
            }
        }
        return fragment
    }

}