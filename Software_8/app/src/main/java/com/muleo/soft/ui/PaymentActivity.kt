package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivityPaymentBinding
import com.muleo.soft.util.FirebaseUtil

class PaymentActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore

    // View
    private lateinit var content: TextView
    private lateinit var binding: ActivityPaymentBinding


    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        fb = FirebaseUtil.fireStore

        // View 를 init
        binding.apply {
            content = contentTextView
        }
        fb.collection("camps")
            .document(intent.getStringExtra("campId")!!)
            .collection("reserves")
            .document(intent.getStringExtra("reserveId")!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val data = task.result
                    if (data != null) {
                        Log.d("PaymentActivity", "${data}")
                        content.text = getString(
                            R.string.reserve_result,
                            data.getTimestamp("start")!!.toDate().toString(), // 시작 날짜
                            data.getTimestamp("end")!!.toDate().toString(), // 종료 날짜
                            data.getLong("count").toString(), // 인원수
                            data.getString("campName"), // 캠핑장 이름
                            data.getLong("totalPrice").toString(), // 총 결제 가격격
                            data.getString("userId"),
                        )
                    }
                }
            }
        fb.collection("camps")
            .document(intent.getStringExtra("campId")!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val data = task.result
                    if (data != null) {
                        Log.d("PaymentActivity", "${data}")
                        binding.accountAddressTextView.text =
                            getString(R.string.payment_account, data.getString("accountNumber"))
                    }
                }
            }
    }

    // 내 예약 목록을 눌렀을 시에
    fun reserveList(view: View) {
        finish()
        startActivity(Intent(this, ReserveListActivity::class.java))
    }

    // 확인 버튼을 눌렀을 시에
    fun confirm(view: View) {
        finish()
    }
}