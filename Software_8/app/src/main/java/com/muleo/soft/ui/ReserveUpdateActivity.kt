package com.muleo.soft.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivityReserveUpdateBinding
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import java.util.*

class ReserveUpdateActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var count: EditText
    private lateinit var binding: ActivityReserveUpdateBinding

    // 시작, 끝
    private lateinit var start: Timestamp
    private lateinit var end: Timestamp

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReserveUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        fb = FirebaseUtil.fireStore
        auth = AuthUtil.auth

        // View 를 init
        binding.apply {
            startDate = etFrom
            endDate = etTo
            count = etCount
            tiFrom.setEndIconOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select start date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                        .apply {
                            addOnPositiveButtonClickListener { date ->
                                Log.d(
                                    "MenuFragmentReserve",
                                    "${date} + ${Timestamp(Date(date)).toDate()}"
                                )
                                val timePicker =
                                    MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_12H)
                                        .setHour(12)
                                        .setMinute(0)
                                        .build()

                                timePicker.addOnPositiveButtonClickListener {
                                    val resultTime = Timestamp(Date(date).apply {
                                        hours = timePicker.hour
                                        minutes = timePicker.minute
                                    })
                                    startDate.setText(resultTime.toDate().toString())
                                    start = resultTime
                                }
                                timePicker.show(parentFragmentManager, "selectStartTime")
                            }
                        }
                datePicker.show(supportFragmentManager, "selectStartDate")
            }
            tiTo.setEndIconOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select end date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                        .apply {
                            addOnPositiveButtonClickListener { date ->
                                Log.d(
                                    "MenuFragmentReserve",
                                    "${date} + ${Timestamp(Date(date)).toDate()}"
                                )
                                val timePicker =
                                    MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_12H)
                                        .setHour(12)
                                        .setMinute(0)
                                        .build()

                                timePicker.addOnPositiveButtonClickListener {
                                    val resultTime = Timestamp(Date(date).apply {
                                        hours = timePicker.hour
                                        minutes = timePicker.minute
                                    })
                                    endDate.setText(resultTime.toDate().toString())
                                    end = resultTime
                                }
                                timePicker.show(parentFragmentManager, "selectEndTime")
                            }
                        }
                datePicker.show(supportFragmentManager, "selectEndDate")
            }
        }
        // 사람 인원수
        val items = (1..100).toList()
        (count as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                baseContext,
                R.layout.count,
                items
            )
        )

        // 인텐트로부터 view 에 데이터 할당
        startDate.setText(intent.getParcelableExtra<Timestamp>("end")?.toDate().toString())
        endDate.setText(intent.getParcelableExtra<Timestamp>("start")?.toDate().toString())
        count.setText(intent.getLongExtra("count", 1).toString())


        binding.cancel.setOnClickListener(::cancelReserve)
        binding.update.setOnClickListener(::updateReserve)
    }


    // 수정 버튼을 눌렀을떄
    fun updateReserve(view: View) {
        Log.d("ReserveUpdateActivity", "수정 버튼을 누름!")
        fb.collection("camps")
            .whereEqualTo("name", intent.getStringExtra("campName"))
            .limit(1)
            .get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val campId =
                        task.result!!.documents[0].getString("id")
                    fb.collection("camps")
                        .document(campId!!)
                        .collection("reserves")
                        .document(intent.getStringExtra("reserveId")!!)
                        .update(
                            mapOf(
                                "start" to if (this::start.isInitialized) {
                                    start
                                } else {
                                    intent.getParcelableExtra<Timestamp>(
                                        "start"
                                    )!!
                                },
                                "end" to if (this::end.isInitialized) {
                                    end
                                } else {
                                    intent.getParcelableExtra<Timestamp>(
                                        "end"
                                    )!!
                                },
                                "count" to if (this::count.isInitialized) {
                                    count.text.toString().toLong()
                                } else {
                                    intent.getLongExtra("count", 0)
                                },
                            )
                        )
                        .addOnCompleteListener { task2 ->
                            if (!task2.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "수정 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.d("ReserveUpdateActivity", "수정성공!!")
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "수정 실패",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }

    // 취소 버튼을 눌렀을때
    fun cancelReserve(view: View) {
        finish()
    }
}
