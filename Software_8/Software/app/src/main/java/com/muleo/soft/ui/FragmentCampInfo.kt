package com.muleo.soft.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muleo.soft.R
import com.muleo.soft.control.CampControl
import com.muleo.soft.databinding.FragmentCampInfoBinding

class FragmentCampInfo : Fragment(R.layout.fragment_camp_info) {

    private val cc: CampControl by lazy {
        ViewModelProvider(requireActivity()).get(CampControl::class.java)
    }
    private lateinit var photo: ImageView
    private lateinit var title: TextView
    private lateinit var info: TextView
    private var _binding: FragmentCampInfoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCampInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.apply {
            photo = photoImageView
            title = titleEditText
            info = infoEditText
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO 일단 공학관만 가져오는 것으로 설정하였다.
        cc.checkCamp.observe(viewLifecycleOwner, { c ->
            if (c != null) {
                // 1. 사진 설정
                Glide.with(this)
                    .load(c.imgPath)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_baseline_broken_image)
                    ).into(photo)
                // 2. 제목 및 설명
                title.text = c.name
                info.text = c.inf
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}