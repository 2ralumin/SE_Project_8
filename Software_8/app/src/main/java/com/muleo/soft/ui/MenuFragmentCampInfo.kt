package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.muleo.soft.R
import com.muleo.soft.databinding.MenuFragmentCampInfoBinding
import com.muleo.soft.entity.Camp
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil

class MenuFragmentCampInfo : Fragment(R.layout.menu_fragment_camp_info) {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var rc: RecyclerView
    private lateinit var error: Chip

    private var _binding: MenuFragmentCampInfoBinding? = null
    private val binding get() = _binding!!

    private val isManager by lazy {
        if (auth.currentUser != null) {
            auth.currentUser!!.displayName == "manager"
        } else {
            false
        }
    }

    // 생명주기 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuFragmentCampInfoBinding.inflate(inflater, container, false)

        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            rc = campRecyclerview
            error = campInfoError.apply {
                setOnClickListener {
                    startActivity(Intent(context, CampWriteActivity::class.java))
                }
            }
        }

        // Firebase UI
        val query = fb.collection("camps")
            .orderBy("id") // id 순 정렬
            .limit(9) // 9개 제한

        val options = FirestoreRecyclerOptions.Builder<Camp>()
            .setLifecycleOwner(viewLifecycleOwner) // Automatic listening
            .setQuery(query, Camp::class.java)
            .build()

        val adapter = object : FirestoreRecyclerAdapter<Camp, CampHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampHolder {
                val view = inflater.inflate(R.layout.info_camp, parent, false)
                return CampHolder(view)
            }

            override fun onBindViewHolder(holder: CampHolder, position: Int, model: Camp) {
                holder.itemView.apply {
                    Glide.with(this)
                        .load(model.imgPath)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_baseline_broken_image)
                        ).into(findViewById<ImageView>(R.id.photoImageView))
                    findViewById<TextView>(R.id.nameEditText).text = model.name
                    findViewById<TextView>(R.id.addressEditText).text = model.address
                    findViewById<TextView>(R.id.infoEditText).text = model.info
                    if (model.isEmpty!!) {
                        findViewById<Chip>(R.id.chip_unavailable).alpha = 0.25f
                    } else {
                        findViewById<Chip>(R.id.chip_available).alpha = 0.25f
                    }
                    // 관리자 일 경우
                    if (isManager as Boolean) {
                        isClickable = true
                        findViewById<Chip>(R.id.chip_edit).apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                // 캠핑장 수정 로직
                                startActivity(
                                    Intent(
                                        context,
                                        CampUpdateActivity::class.java
                                    ).apply {
                                        putExtra("campId", model.id)
                                    })
                            }
                        }
                    }
                    setOnClickListener { //
                        startActivity(
                            Intent(
                                context,
                                CampUpdateActivity::class.java
                            ).apply {
                                putExtra("campId", model.id)
                            })
                    }
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0) {
                    error.visibility = View.VISIBLE
                    if (isManager as Boolean) {
                        error.isClickable = true
                        error.text = "캠핑장 정보 추가"
                    } else {
                        error.isClickable = false
                        error.text = "현재 캠핑장 정보가 없습니다!"
                    }
                } else {
                    error.visibility = View.INVISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                super.onError(e)
                error.visibility = View.VISIBLE
                error.text = e.message
                error.isClickable = false
            }

        }
        rc.adapter = adapter
        rc.setHasFixedSize(true)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CampHolder(view: View) : RecyclerView.ViewHolder(view)
}
