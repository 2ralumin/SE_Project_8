package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muleo.soft.databinding.MenuFragmentBoardBinding
import com.muleo.soft.entity.PostType

class MenuFragmentBoard : Fragment() {

    // View
    private var _binding: MenuFragmentBoardBinding? = null
    private val binding get() = _binding!!

    // 생명주기 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuFragmentBoardBinding.inflate(inflater, container, false)
        binding.apply {
            post.setOnClickListener {
                startActivity(Intent(context, BoardActivity::class.java).apply {
                    putExtra("PostType", PostType.POST.ordinal)
                })
            }
            postLike.setOnClickListener {
                startActivity(Intent(context, BoardActivity::class.java).apply {
                    putExtra("PostType", PostType.LIKE.ordinal)
                })
            }
            postDislike.setOnClickListener {
                startActivity(Intent(context, BoardActivity::class.java).apply {
                    putExtra("PostType", PostType.DISLIKE.ordinal)
                })
            }
            postNotice.setOnClickListener {
                startActivity(Intent(context, BoardActivity::class.java).apply {
                    putExtra("PostType", PostType.NOTICE.ordinal)
                })
            }
            postReview.setOnClickListener {
                startActivity(Intent(context, BoardActivity::class.java).apply {
                    putExtra("PostType", PostType.REVIEW.ordinal)
                })
            }
            postQna.setOnClickListener {

            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}