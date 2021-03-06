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

    // ?????????
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

        // View ??? init
        binding.apply {
            writer = postWriter.apply {
                fb.collection("users").document("${post.userId}")
                    .get()
                    .addOnSuccessListener {
                        text = (it.data?.get("name") as String)
                    }
            }
            toggle = materialButtonToggleGroup.apply {
                // post ??? ?????? ?????? ??????
                val e = auth.currentUser!!.email
                if (!post.likesId.isNullOrEmpty() && e in post.likesId!!) { // ????????? ???????????? ????????????
                    this.check(R.id.upButton)
                } else if (!post.dislikesId.isNullOrEmpty() && e in post.dislikesId!!) {
                    this.check(R.id.downButton)
                }
                // ?????? ?????? ???
                addOnButtonCheckedListener { _, checkedId, isChecked ->
                    when (checkedId) {
                        R.id.upButton -> { // ????????????
                            if (isChecked) {

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("like", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        // ????????? ???????????? ??????????????? ????????? ???????????? ??? ????????? ?????????????????????.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("likesId", FieldValue.arrayUnion("$e"))
                                        // ?????? ?????? ??? ?????? ???????????? ?????? ??? ??? ??? ?????? ???????????? ?????????.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "???????????????. ????????? ???????????????.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                            } else {
                                // ?????? ????????? ???

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("like", FieldValue.increment(-1))
                                    .addOnSuccessListener {
                                        // ???????????? ?????????????????? ????????? ???????????? ??? ????????? ?????????????????????.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("likesId", FieldValue.arrayRemove("$e"))
                                        // ?????? ?????? ??? ?????? ???????????? ?????? ??? ??? ??? ?????? ????????????.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "???????????????. ????????? ???????????????.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                            }
                        }
                        R.id.downButton -> { // ???????????? ???????????? ???
                            if (isChecked) {

                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("dislike", FieldValue.increment(1))
                                    .addOnSuccessListener {
                                        // ????????? ???????????? ??????????????? ????????? ???????????? ??? ????????? ?????????????????????.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update(
                                                "dislikesId",
                                                FieldValue.arrayUnion("$e")
                                            )
                                        // ?????? ?????? ??? ?????? ???????????? ?????? ??? ??? ??? ?????? ???????????? ?????????.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "???????????????. ???????????? ???????????????.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                            } else {

                                // ?????? ????????????
                                fb.collection("boards")
                                    .document(BoardType.values()[post.type!!].name)
                                    .collection("posts")
                                    .document(post.id!!)
                                    .update("dislike", FieldValue.increment(-1))
                                    .addOnSuccessListener {
                                        // ???????????? ?????????????????? ????????? ???????????? ??? ????????? ?????????????????????.
                                        fb.collection("boards")
                                            .document(BoardType.values()[post.type!!].name)
                                            .collection("posts")
                                            .document(post.id!!)
                                            .update("dislikesId", FieldValue.arrayRemove("$e"))
                                        // ?????? ?????? ??? ?????? ???????????? ?????? ??? ??? ??? ?????? ????????????.
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "???????????????. ???????????? ???????????????.",
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
                    Snackbar.make(it, "????????? ???????????????", Snackbar.LENGTH_SHORT)
                        .setAction("??????") {}.show()
                } else {
                    // TODO ?????? ??????
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
                                // ????????? ??????
                                val imm: InputMethodManager =
                                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(comment.windowToken, 0)
                                comment.text = null
                            }

                        }

                }
            }
        }

        // ?????? ?????? - ????????? ??? ?????? ?????? ??? ?????? ????????????
        if (auth.currentUser!!.email == post.userId) { // ???????????????.
            requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).apply {
                menu.clear()
                inflateMenu(R.menu.menu_post)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.update_post -> {
                            // ???????????? ?????? ????????? ??????
                            val action =
                                BoardFragmentPostDirections.actionBoardFragmentPostToBoardFragmentUpdate(
                                    post = post
                                )
                            binding.root.findNavController().navigate(action)
                            true
                        }
                        R.id.delete_post -> {
                            // ?????? ????????? ?????? ?????????.
                            MaterialAlertDialogBuilder(context)
                                .setTitle("?????? ?????????????????????????")
                                .setMessage("?????? ????????? ????????? ??? ????????????.")
                                .setNegativeButton("?????????.") { _, _ -> }
                                .setPositiveButton("???, ???????????????.") { _, _ ->
                                    fb.collection("boards")
                                        .document(BoardType.values()[post.type!!].name)
                                        .collection("posts")
                                        .document(post.id!!)
                                        .delete()
                                        .addOnSuccessListener {
                                            // ????????????
                                            binding.root.findNavController().popBackStack()
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "???????????????. ????????? ??????????????????.",
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

        // ?????? ????????? ??????
        val query = fb.collection("boards").document(BoardType.values()[post.type!!].name)
            .collection("posts")
            .document(post.id!!).collection("comments")
            .orderBy("date") // ?????? ??????
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
        // ????????? ???????????? ??????
        registration = fb.collection("boards")
            .document(BoardType.values()[post.type!!].name)
            .collection("posts")
            .document(post.id!!)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    Log.d("BoardFragmentPost", "????????? ??????..")
                    val data = snapshot.data
                    title.text = data?.get("title")!! as String
                    content.text = data["post"]!! as String
                    up.text = "${data["like"] as Long}"
                    down.text = "${data["dislike"] as Long}"
                    date.text = (data["date"]!! as Timestamp).toDate().toString()
                    //TODO ??????
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        registration.remove()
    }

    class CommentHolder(view: View) : RecyclerView.ViewHolder(view)
}