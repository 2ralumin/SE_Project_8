package com.muleo.soft.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.BoardFragmentUpdateBinding
import com.muleo.soft.entity.BoardType
import com.muleo.soft.util.FirebaseUtil
import java.util.*

class BoardFragmentUpdate : Fragment() {
    // Firebase
    private lateinit var fb: FirebaseFirestore

    //View
    private lateinit var title: EditText
    private lateinit var context: EditText
    private lateinit var addImage: ImageView
    private var _binding: BoardFragmentUpdateBinding? = null
    private val binding get() = _binding!!

    // Argument
    private val args: BoardFragmentUpdateArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BoardFragmentUpdateBinding.inflate(inflater, container, false)

        val boardName = BoardType.values()[args.post.type!!].name // 해당하는 게시판 document


        // Firebase
        fb = FirebaseUtil.fireStore

        // View 를 init
        binding.apply {
            title = editTextTitle
            context = editTextContext
            addImage = addImageButton
        }

        title.setText(args.post.title)
        context.setText(args.post.post)


        // 메뉴 설정
        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
            menu.clear()
            inflateMenu(R.menu.menu_update_post)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.complete_update_post -> {
                        val boardsRef = fb.collection("boards")
                        boardsRef.document(boardName).collection("posts")
                            .document(args.post.id!!)
                            .update(
                                mapOf(
                                    "title" to this@BoardFragmentUpdate.title.text.toString(),
                                    "post" to this@BoardFragmentUpdate.context.text.toString(),
                                    "date" to Timestamp.now(),
                                    // TODO 이미지 업데이트
                                )
                            ).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    binding.root.findNavController().popBackStack()
                                } else {
                                    Log.d("BoardFragmentUpdate", "업데이트 실패${task.exception}")
                                    Toast.makeText(context, "죄송합니다. 업데이트가 불가능 합니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

        return binding.root
    }
}