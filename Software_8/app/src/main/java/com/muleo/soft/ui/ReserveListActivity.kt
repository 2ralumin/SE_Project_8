package com.muleo.soft.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivityReserveListBinding
import com.muleo.soft.entity.Reserve
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import java.util.*

class ReserveListActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var rc: RecyclerView
    private lateinit var title: TextView
    private lateinit var binding: ActivityReserveListBinding

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReserveListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            rc = reserveRecyclerview
            title = reserveListTitle
        }

        // Firebase UI
        val query = if (auth.currentUser!!.displayName != "manager") { // 관리자가 아니면
            title.text = "예약목록"
            fb.collectionGroup("reserves")
                .whereEqualTo("userId", auth.currentUser!!.email)
                .orderBy("reserveDate", Query.Direction.DESCENDING)
        } else { // 관리자면
            title.text = "예약관리"
            fb.collectionGroup("reserves")
                .orderBy("reserveDate", Query.Direction.DESCENDING)
        }

        val options = FirestoreRecyclerOptions.Builder<Reserve>()
            .setLifecycleOwner(this) // Automatic listening
            .setQuery(query, Reserve::class.java)
            .build()

        val adapter = object : FirestoreRecyclerAdapter<Reserve, ReserveHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReserveHolder {
                val view = if (auth.currentUser!!.displayName != "manager") { // 관리자가 아니면
                    layoutInflater.inflate(R.layout.info_reserve, parent, false)
                } else { // 관리자 라면
                    layoutInflater.inflate(R.layout.info_reserve_manager, parent, false)
                }
                return ReserveHolder(view)
            }

            override fun onBindViewHolder(holder: ReserveHolder, position: Int, model: Reserve) {
                holder.itemView.apply {
                    findViewById<TextView>(R.id.reserveDate).text =
                        getString(
                            R.string.info_reserve_reserveDate,
                            model.reserveDate?.toDate().toString()
                        )
                    findViewById<TextView>(R.id.acceptDate).text =
                        getString(
                            R.string.info_reserve_acceptDate,
                            model.acceptDate?.toDate().toString()
                        )
                    findViewById<TextView>(R.id.start).text =
                        getString(R.string.info_reserve_start, model.start?.toDate().toString())
                    findViewById<TextView>(R.id.end).text =
                        getString(R.string.info_reserve_end, model.end?.toDate().toString())
                    findViewById<TextView>(R.id.campName).text = model.campName
                    findViewById<TextView>(R.id.count).text =
                        getString(R.string.info_reserve_count, model.count?.toString())
                    findViewById<TextView>(R.id.totalPrice).text =
                        getString(R.string.info_reserve_totalPrice, model.totalPrice?.toString())
                    val p = model.isAccept
                    var str = if (p == true) {
                        "수락됨"
                    } else {
                        "대기중"
                    }
                    if (auth.currentUser!!.displayName == "manager") {
                        // 관리자라면
                        if (str == "대기중") {
                            findViewById<TextView>(R.id.acceptButton).apply {
                                visibility = View.VISIBLE
                                setOnClickListener {
                                    // 클릭시 수락을 진행함
                                    MaterialAlertDialogBuilder(it.context)
                                        .setTitle("수락 하시겠습니까?")
                                        .setNeutralButton("취소") { dialog, which ->
                                            // Respond to neutral button press
                                        }
                                        .setPositiveButton("수락") { dialog, which ->
                                            fb.collection("camps")
                                                .whereEqualTo("name", model.campName).limit(1)
                                                .get()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        val campId =
                                                            task.result!!.documents[0].getString("id")
                                                        fb.collection("camps")
                                                            .document(campId!!)
                                                            .collection("reserves")
                                                            .document(model.id!!)
                                                            .update(
                                                                mapOf(
                                                                    "isAccept" to true,
                                                                    "acceptDate" to Timestamp.now(),
                                                                )
                                                            )
                                                            .addOnCompleteListener { task2 ->
                                                                if (!task2.isSuccessful) {
                                                                    Toast.makeText(
                                                                        this@ReserveListActivity,
                                                                        "수락 실패",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                    } else {
                                                        Toast.makeText(
                                                            this@ReserveListActivity,
                                                            "수락 실패",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                                }
                                        }
                                        .show()
                                }
                            }
                            str = ""
                        }
                    }
                    findViewById<TextView>(R.id.isAccept).text = str
                }
            }
        }

        rc.adapter = adapter
    }

    class ReserveHolder(view: View) : RecyclerView.ViewHolder(view)

}