package com.muleo.soft.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.muleo.soft.R
import com.muleo.soft.databinding.BoardFragmentBinding
import com.muleo.soft.entity.BoardType
import com.muleo.soft.entity.Post
import com.muleo.soft.entity.PostType
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil

class BoardFragment : Fragment() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var rc: RecyclerView
    private lateinit var error: TextView
    private var _binding: BoardFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("BoardFragment", "동작")
        _binding = BoardFragmentBinding.inflate(inflater, container, false)

        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            rc = postRecyclerview
            error = boardInfoError
        }

        // Firebase UI
        when (requireArguments().getInt("postType", 4)) {
            PostType.POST.ordinal -> {
                val query = fb.collectionGroup("posts").whereEqualTo(
                    "userId",
                    "${auth.currentUser?.email}"
                )
                    .orderBy("date", Query.Direction.DESCENDING) // date 순 정렬
                    .limit(50) // 50개 제한
                setAdapter(inflater, query)
            }
            PostType.LIKE.ordinal -> {
                val query = fb.collectionGroup("posts").whereArrayContains(
                    "likesId",
                    "${auth.currentUser?.email}"
                )
                    .orderBy("date", Query.Direction.DESCENDING) // date 순 정렬
                    .limit(50) // 50개 제한
                setAdapter(inflater, query)
            }
            PostType.DISLIKE.ordinal -> {
                val query = fb.collectionGroup("posts").whereArrayContains(
                    "dislikesId",
                    "${auth.currentUser?.email}"
                )
                    .orderBy("date", Query.Direction.DESCENDING) // date 순 정렬
                    .limit(50) // 50개 제한
                setAdapter(inflater, query)
            }
            PostType.NOTICE.ordinal -> {
                val query =
                    fb.collection("boards").document(BoardType.NOTICE.name).collection("posts")
                        .orderBy("date", Query.Direction.DESCENDING) // date 순 정렬
                        .limit(50) // 50개 제한
                setAdapter(inflater, query)
                // 메뉴 설정 - 글쓰기
                requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
                    menu.clear()
                    if (auth.currentUser!!.displayName == "manager") { // 공지는 관리자만 쓸 수 있도록
                        inflateMenu(R.menu.menu_write_post)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.write_post -> {
                                    val action =
                                        BoardFragmentDirections.actionBoardFragmentToBoardFragmentWrite(
                                            title = "공지 글 쓰기",
                                            boardType = BoardType.NOTICE.ordinal
                                        )
                                    binding.root.findNavController().navigate(action)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    }
                }

            }
            PostType.REVIEW.ordinal -> {
                val query =
                    fb.collection("boards").document(BoardType.REVIEW.name).collection("posts")
                        .orderBy("date", Query.Direction.DESCENDING) // date 순 정렬
                        .limit(50) // 50개 제한
                setAdapter(inflater, query)
                // 메뉴 설정 - 글쓰기
                requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
                    menu.clear()
                    if (auth.currentUser!!.displayName != "manager") {
                        inflateMenu(R.menu.menu_write_post)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.write_post -> {
                                    Log.d("BoardFragment", "됨?")
                                    val action =
                                        BoardFragmentDirections.actionBoardFragmentToBoardFragmentWrite(
                                            title = "리뷰 글 쓰기",
                                            boardType = BoardType.REVIEW.ordinal
                                        )
                                    binding.root.findNavController().navigate(action)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
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


    // 해당하는 타입에 따른 adapter를 설정 하는 부분
    private fun setAdapter(inflater: LayoutInflater, query: Query) {
        val options = FirestoreRecyclerOptions.Builder<Post>()
            .setLifecycleOwner(viewLifecycleOwner) // Automatic listening
            .setQuery(query, Post::class.java)
            .build()

        val adapter =
            object : FirestoreRecyclerAdapter<Post, PostHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PostHolder {
                    val view = inflater.inflate(R.layout.info_post, parent, false)
                    return PostHolder(view)
                }

                override fun onBindViewHolder(
                    holder: PostHolder,
                    position: Int,
                    model: Post
                ) {
                    holder.itemView.apply {
                        findViewById<TextView>(R.id.postTitleTextView).text = model.title
                        findViewById<TextView>(R.id.postContentTextView).text = model.post
                        val meta = "${model.date!!.toDate()}\n${model.userId}"
                        findViewById<TextView>(R.id.postMetaTextView).text = meta
                        findViewById<Button>(R.id.thumbUpButton).apply {
                            if (model.like == 0L) {
                                alpha = 0.2f
                                text = "0"
                            } else {
                                text = "${model.like}"
                            }
                        }
                        findViewById<Button>(R.id.imageButton).apply {
                            if (model.like == 0L) {
                                alpha = 0.2f
                                text = "0"
                            } else {
                                text = "${model.images?.size ?: 0}"
                            }
                        }
                        setOnClickListener { // 글 클릭 시
                            requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).menu.clear()
                            val action =
                                BoardFragmentDirections.actionBoardFragmentToBoardFragmentPost(post = model)
                            this.findNavController().navigate(action)
                        }
                    }
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    if (itemCount == 0) {
                        error.visibility = View.VISIBLE
                        error.text = "글 정보가 없습니다."
                    } else {
                        error.visibility = View.INVISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    super.onError(e)
                    error.visibility = View.VISIBLE
                    error.text = e.message
                }

            }
        rc.adapter = adapter
        rc.setHasFixedSize(true)
    }


    class PostHolder(view: View) : RecyclerView.ViewHolder(view)
}