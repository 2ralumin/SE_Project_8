package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.muleo.soft.R
import com.muleo.soft.databinding.MenuFragmentReserveBinding
import com.muleo.soft.entity.Camp
import com.muleo.soft.entity.Reserve
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import kotlinx.android.synthetic.main.menu_fragment_reserve.*
import java.util.*
import kotlin.collections.ArrayList

class MenuFragmentReserve : Fragment() {

    // Firebase
    private lateinit var fb: FirebaseFirestore

    // View
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var count: EditText
    private lateinit var selectCamp: EditText
    private lateinit var campImage: ImageView
    private lateinit var campName: TextView
    private lateinit var campAddress: TextView
    private lateinit var campInfo: TextView
    private lateinit var reserve: Button
    private var _binding: MenuFragmentReserveBinding? = null
    private val binding get() = _binding!!

    // 시작, 끝, 캠핑장 리스트
    private lateinit var start: Timestamp
    private lateinit var end: Timestamp
    private lateinit var camps: ArrayList<Camp>
    private lateinit var camp: Camp
    private var cou: Long? = null
    private var cam: String? = null

    // 실시간
    private lateinit var registration: ListenerRegistration

    // 생명주기 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuFragmentReserveBinding.inflate(inflater, container, false)

        // Firebase
        fb = FirebaseUtil.fireStore

        // View 를 init
        binding.apply {
            startDate = etFrom
            endDate = etTo
            count = etCount
            selectCamp = etCamp
            campInfo = campInfoTextView
            campName = campNameTextView
            campAddress = campAddressTextView
            campImage = campImageView
            reserve = reserveButton

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
                datePicker.show(parentFragmentManager, "selectStartDate")
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
                datePicker.show(parentFragmentManager, "selectEndDate")
            }
        }

        // 사람 인원수
        val items = (1..100).toList()
        (count as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.count,
                items
            )
        )


        // 캠핑 선택

        // 실시간
        registration = fb.collection("camps")
            .whereEqualTo("isEmpty", true)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val camps = ArrayList<Camp>()
                for (doc in snapshot!!) {
                    camps.add(
                        Camp(
                            id = doc.getString("id"),
                            name = doc.getString("name"),
                            latitude = doc.getDouble("latitude"),
                            longitude = doc.getDouble("longitude"),
                            address = doc.getString("address"),
                            isEmpty = doc.getBoolean("isEmpty"),
                            imgPath = doc.getString("imgPath"),
                            accountNumber = doc.getString("accountNumber"),
                            info = doc.getString("info"),
                            date = doc.getTimestamp("date"),
                            won = doc.getLong("won"),
                        )
                    )
                }
                this.camps = camps
                // 캠핑장 어뎁터도 설정
                (selectCamp as? AutoCompleteTextView)?.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.count,
                        camps.map { it.name })
                )
            }

        // 인원 수 및 선택된 캠핑장 리스너
        count.doAfterTextChanged {
            if (count.text.toString() != "0") {
                Log.d("MenuFragmentReserve", "인원수 선택됨 ${count.text}")
                cou = "${count.text}".toLong()
            }
        }
        selectCamp.doAfterTextChanged {
            if (selectCamp.text.toString() != "") {
                Log.d("MenuFragmentReserve", "캠핑장 선택됨 ${selectCamp.text}")
                cam = "${selectCamp.text}"

                // 해당하는 이름에 따른 캠핑장을 표시함
                val camp: Camp? = camps.find { it.name == cam }
                if (camp != null) {
                    this.camp = camp
                    campInfo.text = camp.info
                    campName.text = camp.name
                    campAddress.text = camp.address
                    Glide.with(this)
                        .load(camp.imgPath)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_baseline_broken_image)
                        ).into(campImage)
                }
            }
        }

        // 예약하기 버튼이 눌렸을 시
        reserve.setOnClickListener {
            // 검사
            if (!this::start.isInitialized || startDate.text.isNullOrEmpty()) { // 시작 날짜
                startDate.requestFocus()
                return@setOnClickListener
            }
            if (!this::end.isInitialized || endDate.text.isNullOrEmpty()) { // 끝 날짜
                endDate.requestFocus()
                return@setOnClickListener
            }
            if (!this::camps.isInitialized) {
                Toast.makeText(context, "캠핑장 배열 정보 오류", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (count.text.isNullOrEmpty() || count.text.toString() == "0") {
                Toast.makeText(context, "인원 오류", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!this::camp.isInitialized || selectCamp.text.isNullOrEmpty()) {
                Toast.makeText(context, "캠핑장이 선택되지 않음", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Dialog 로 예약 정보 확인
            MaterialAlertDialogBuilder(it.context)
                .setTitle("예약 정보 확인")
                .setMessage(
                    resources.getString(
                        R.string.reserve_confirm,
                        start.toDate().toString(), // 시작 날짜
                        end.toDate().toString(), // 종료 날짜
                        count.text.toString(), // 인원수
                        this.camp.name, // 캠핑장 이름
                        (this.camp.won!! * count.text.toString().toInt()).toString() // 총 결제 가격격
                    )
                )
                .setNeutralButton("취소") { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton("승인") { dialog, which ->
                    // 예약 정보 저장 후 결과 엑티비티로 이동
                    val r = Reserve(
                        id = "${UUID.randomUUID()}",
                        userId = AuthUtil.auth.currentUser!!.email,
                        count = count.text.toString().toLong(),
                        totalPrice = (this.camp.won!! * count.text.toString().toInt()),
                        isAccept = false,
                        start = start,
                        end = end,
                        reserveDate = Timestamp.now(),
                        acceptDate = null,
                        campName = camp.name,
                    )
                    fb.collection("camps")
                        .document(camp.id!!) //  제 1공학관의 예약 정보.
                        .collection("reserves")
                        .document(r.id!!)
                        .set(r)
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(context, "에약정보 저장불가.", Toast.LENGTH_SHORT).show()
                            } else {
                                // 정리
                                startDate.text = null
                                endDate.text = null
                                count.setText("0")
                                selectCamp.setText("")
                                campInfo.text = null
                                campName.text = null
                                campAddress.text = null
                                campImage.setImageDrawable(null)

                                // 이동
                                startActivity(
                                    Intent(context, PaymentActivity::class.java).putExtra(
                                        "reserveId",
                                        r.id
                                    ).putExtra("campId", camp.id)
                                )

                            }
                        }

                }
                .show()

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}