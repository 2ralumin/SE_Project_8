package com.muleo.soft.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.BoardFragmentWriteBinding
import com.muleo.soft.entity.BoardType
import com.muleo.soft.entity.Post
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import java.util.*

class BoardFragmentWrite : Fragment() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    //View
    private lateinit var title: EditText
    private lateinit var context: EditText
    private lateinit var addImage: ImageView
    private var _binding: BoardFragmentWriteBinding? = null
    private val binding get() = _binding!!

    // Argument
    private val args: BoardFragmentWriteArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BoardFragmentWriteBinding.inflate(inflater, container, false)

        val boardName = BoardType.values()[args.boardType].name // 해당하는 게시판 document


        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            title = editTextTitle
            context = editTextContext
            addImage = addImageButton
        }


        // 메뉴 설정
        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
            menu.clear()

            inflateMenu(R.menu.menu_write_post2)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.complete_write_post -> {
                        val boardsRef = fb.collection("boards")
                        val p = Post(
                            id = "${UUID.randomUUID()}",
                            type = args.boardType,
                            title = this@BoardFragmentWrite.title.text.toString(),
                            post = this@BoardFragmentWrite.context.text.toString(),
                            date = Timestamp.now(),
                            like = 0,
                            dislike = 0,
                            userId = auth.currentUser!!.email,
                            images = null,
                            likesId = null,
                            dislikesId = null,
                        )
                        boardsRef.document(boardName).collection("posts")
                            .document(p.id!!)
                            .set(p)
                        binding.root.findNavController().popBackStack()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}