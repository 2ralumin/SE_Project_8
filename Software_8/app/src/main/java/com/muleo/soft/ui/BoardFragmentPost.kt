package com.muleo.soft.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.muleo.soft.R
import com.muleo.soft.databinding.BoardFragmentPostBinding
import com.muleo.soft.entity.BoardType
import com.muleo.soft.entity.Comment
import com.muleo.soft.entity.Post
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import java.util.*


class BoardFragmentPost : Fragment() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var writer: TextView
    private lateinit var date: TextView
    private lateinit var toggle: MaterialButtonToggleGroup
    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var up: Button
    private lateinit var down: Button
    private lateinit var comment: EditText
    private lateinit var rc: RecyclerView
    private var _binding: BoardFragmentPostBinding? = null
    private val binding get() = _binding!!

    // Argument
    private val args: BoardFragmentPostArgs by navArgs()
    private lateinit var post: Post

    // 실시간
    private lateinit var registration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BoardFragmentPostBinding.inflate(inflater, container, false)
        post = args.post
        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            writer = postWriter.apply {
                fb.collection("users").document("${post.userId}")
                    .get()
                    .addOnSuccessListener {
                        text = (it.data?.get("name") as String)
                    }
            }
            toggle = materialButtonToggleGroup.apply {
                // post 에 따른 토글 설정
                val e = auth.currentUser!!.email
                if (!post.likesId.isNullOrEmpty() && e in post.likesId!!) { // 이글에 좋아요를 눌렀다면
                    this.check(R.id.upButton)
                } else if (!post.dislikesId.isNullOrEmpty() && e in post.dislikesId!!) {
                    this.check(R.id.downButton)
                }
                // 토글 클릭 시
                addOnButtonCheckedListener { _, checkedId, isChecked ->
                    when (checkedId) {
                        R.id.upButton -> { // 좋아요가
                            if (isChecked) {

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("like", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        // 좋아요 리스트에 넣음으로써 나중에 좋아요가 더 많아도 수정가능하게끔.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("likesId", FieldValue.arrayUnion("$e"))
                                        // 만약 실패 할 경우 좋아요를 누른 글 에 이 글이 표시되지 않는다.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "죄송합니다. 추천이 불가합니다.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                            } else {
                                // 취소 되었을 때

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("like", FieldValue.increment(-1))
                                    .addOnSuccessListener {
                                        // 리스트에 삭제함으로써 나중에 좋아요가 더 많아도 수정가능하게끔.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("likesId", FieldValue.arrayRemove("$e"))
                                        // 만약 실패 할 경우 좋아요를 누른 글 에 이 글이 표시된다.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "죄송합니다. 추천이 불가합니다.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                            }
                        }
                        R.id.downButton -> { // 싫어요를 선택했을 때
                            if (isChecked) {

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("dislike", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        // 좋아요 리스트에 넣음으로써 나중에 싫어요가 더 많아도 수정가능하게끔.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update(
                                                "dislikesId",
                                                FieldValue.arrayUnion("$e")
                                            )
                                        // 만약 실패 할 경우 싫어요를 누른 글 에 이 글이 표시되지 않는다.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "죄송합니다. 비추천이 불가합니다.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                            } else {

                                // 진짜 취소라면
                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("dislike", FieldValue.increment(-1))
                                    .addOnSuccessListener {
                                        // 리스트에 삭제함으로써 나중에 싫어요가 더 많아도 수정가능하게끔.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("dislikesId", FieldValue.arrayRemove("$e"))
                                        // 만약 실패 할 경우 싫어요를 누른 글 에 이 글이 표시된다.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "죄송합니다. 비추천이 불가합니다.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                            }
                        }
                    }
                }
            }
            title = postTitle
            content = postContent
            up = thumbUpButton
            down = thumbDownButton
            date = postDate
            comment = commentEditText
            rc = commentRecyclerview
            commentTextField.setEndIconOnClickListener {
                if (comment.text.isNullOrEmpty()) {
                    Snackbar.make(it, "내용을 입력하세요", Snackbar.LENGTH_SHORT)
                        .setAction("확인") {}.show()
                } else {
                    // TODO 댓글 쓰기
                    val c = Comment(
                        id = "${UUID.randomUUID()}",
                        date = Timestamp.now(),
                        comment = "${comment.text}",
                        userId = "${auth.currentUser!!.email}"
                    )
                    fb.collection("boards").document(BoardType.values()[post.type!!].name).collection("posts")
                        .document(post.id!!).collection("comments")
                        .document("${c.id}")
                        .set(c)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // 키보드 닫기
                                val imm: InputMethodManager =
                                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(comment.windowToken, 0)
                                comment.text = null
                            }

                        }

                }
            }
        }

        // 메뉴 설정 - 글쓴이 인 경우 삭제 및 수정 가능하게
        if (auth.currentUser!!.email == post.userId) { // 글쓴이라면.
            requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
                menu.clear()
                inflateMenu(R.menu.menu_post)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.update_post -> {
                            // 업데이트 하는 곳으로 이동
                            val action =
                                BoardFragmentPostDirections.actionBoardFragmentPostToBoardFragmentUpdate(
                                    post = post
                                )
                            binding.root.findNavController().navigate(action)
                            true
                        }
                        R.id.delete_post -> {
                            // 삭제 의향을 다시 묻는다.
                            MaterialAlertDialogBuilder(context)
                                .setTitle("글을 삭제하시겠습니까?")
                                .setMessage("삭제 후에는 되돌릴 수 없습니다.")
                                .setNegativeButton("아니요.") { _, _ -> }
                                .setPositiveButton("예, 삭제합니다.") { _, _ ->
                                    fb.collection("boards")
                                        .document(BoardType.values()[post.type!!].name)
                                        .collection("posts")
                                        .document(post.id!!)
                                        .delete()
                                        .addOnSuccessListener {
                                            // 뒤로가기
                                            binding.root.findNavController().popBackStack()
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "죄송합니다. 삭제가 불가능합니다.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                                .show()
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
        }

        // 댓글 어댑터 설정
        val query = fb.collection("boards").document(BoardType.values()[post.type!!].name)
            .collection("posts")
            .document(post.id!!).collection("comments")
            .orderBy("date") // 제한 없음
        val options = FirestoreRecyclerOptions.Builder<Comment>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(query, Comment::class.java)
            .build()
        val adapter =
            object : FirestoreRecyclerAdapter<Comment, CommentHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
                    val view = inflater.inflate(R.layout.info_comment, parent, false)
                    return CommentHolder(view)
                }

                override fun onBindViewHolder(
                    holder: CommentHolder,
                    position: Int,
                    model: Comment
                ) {
                    holder.itemView.apply {
                        findViewById<TextView>(R.id.commentTitleTextView).text = model.userId
                        findViewById<TextView>(R.id.commentContentTextView).text = model.comment
                        findViewById<TextView>(R.id.commentMetaTextView).text =
                            "${model.date!!.toDate()}"
                    }
                }
            }
        rc.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 실시간 업데이트 설정
        registration = fb.collection("boards")
            .document(BoardType.values()[post.type!!].name)
            .collection("posts")
            .document(post.id!!)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    Log.d("BoardFragmentPost", "스냅샷 로딩..")
                    val data = snapshot.data
                    title.text = data?.get("title")!! as String
                    content.text = data["post"]!! as String
                    up.text = "${data["like"] as Long}"
                    down.text = "${data["dislike"] as Long}"
                    date.text = (data["date"]!! as Timestamp).toDate().toString()
                    //TODO 그림
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        registration.remove()
    }

    class CommentHolder(view: View) : RecyclerView.ViewHolder(view)
}