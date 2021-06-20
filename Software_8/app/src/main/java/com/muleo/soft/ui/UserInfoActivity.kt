package com.muleo.soft.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.muleo.soft.databinding.ActivityUserInfoBinding
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil

class UserInfoActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var date: TextView
    private lateinit var isManager: TextView
    private lateinit var age: EditText
    private lateinit var phone: EditText
    private lateinit var update: Button
    private lateinit var binding: ActivityUserInfoBinding

    // 실시간
    private lateinit var registration: ListenerRegistration


    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = AuthUtil.auth
        fb = FirebaseUtil.fireStore

        // View 를 init
        binding.apply {
            email = emailTextView
            name = nameTextView
            date = dateTextView
            isManager = isManagerTextView
            age = ageEditText.apply {
                doAfterTextChanged {
                    val t = this.text
                    if (t.isNullOrEmpty()) {
                        userUpdateButton.isEnabled = false
                        ageOutlinedTextField.error = "빈칸 불가"
                    } else {
                        ageOutlinedTextField.error = null
                        userUpdateButton.isEnabled = true
                    }
                }
            }
            phone = phoneEditText.apply {
                doAfterTextChanged {
                    val t = this.text
                    if (t.isNullOrEmpty() || t.length !in 1..13) {
                        userUpdateButton.isEnabled = false
                        phoneOutlinedTextField.error = "빈칸 불가, 1이상 13 이하"
                    } else {
                        phoneOutlinedTextField.error = null
                        userUpdateButton.isEnabled = true
                    }
                }
            }
            update = userUpdateButton.apply {
                isEnabled = false
            }
        }

        // 실시간
        registration = fb.collection("users")
            .document(auth.currentUser!!.email!!)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    Log.d("BoardFragmentPost", "스냅샷 로딩..")
                    val data = snapshot.data
                    email.text = data?.get("email")!! as String
                    name.text = data["name"]!! as String
                    isManager.text = if (data["isManager"] as Boolean) "관리자" else "사용자"
                    age.setText("${data["age"] as Long}")
                    phone.setText(data["phone"] as String)
                    date.text = (data["date"]!! as Timestamp).toDate().toString()
                    //TODO 그림
                }
            }

    }

    fun update(view: View) {
        binding.apply {
            if (!ageOutlinedTextField.error.isNullOrEmpty()) {
                ageOutlinedTextField.requestFocus()
                return
            }
            if (!phoneOutlinedTextField.error.isNullOrEmpty()) {
                phoneOutlinedTextField.requestFocus()
                return
            }
            // 조건 만족 시 업데이트
            fb.collection("users").document(auth.currentUser!!.email!!)
                .update(
                    mapOf(
                        "age" to age.text.toString().toLong(),
                        "phone" to "${phone.text}"
                    )
                )
                .addOnSuccessListener {
                    Snackbar.make(view, "업데이트 완료", Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    MaterialAlertDialogBuilder(this@UserInfoActivity)
                        .setMessage("죄송합니다. 회원정보 업데이트가 불가능 합니다. ${e.message}")
                        .setPositiveButton("확인") { _, _ ->
                        }
                        .show()
                }
            // 키보드 닫기
            val imm: InputMethodManager =
                baseContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        registration.remove()
    }

}