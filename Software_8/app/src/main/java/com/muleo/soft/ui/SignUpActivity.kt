package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivitySignUpBinding
import com.muleo.soft.entity.User
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil
import java.util.*

class SignUpActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var email: EditText
    private lateinit var pw: EditText
    private lateinit var pwCheck: EditText
    private lateinit var name: EditText
    private lateinit var age: EditText
    private lateinit var phone: EditText
    private lateinit var signUp: Button
    private lateinit var binding: ActivitySignUpBinding

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        auth = AuthUtil.auth
        fb = FirebaseUtil.fireStore

        // 로그인 하였다면 Menu 로 이동
        if (auth.currentUser != null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, Menu::class.java))
            finish()
        }

        // 자동 스크롤 리스너
//        val listener = View.OnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                binding.signScrollView.post(Runnable {
//                    binding.signScrollView.scrollTo(0, v.scrollY)
//                })
//            }
//        }

        // View 를 init
        binding.apply {
            email = emailTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.length !in 3..30) {
                        emailOutlinedTextField.error = getString(R.string.error_email_length)
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(t).matches()) {
                        emailOutlinedTextField.error = getString(R.string.error_format)
                    } else {
                        emailOutlinedTextField.error = null
                    }
                }
//                onFocusChangeListener = listener
            }
            pw = pwTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.length !in 6..15) {
                        pwOutlinedTextField.error = getString(R.string.error_pw_length)
                    } else {
                        pwOutlinedTextField.error = null
                    }
                }
//                onFocusChangeListener = listener
            }
            pwCheck = pwCheckTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    when {
                        t.length !in 6..15 -> {
                            pwCheckOutlinedTextField.error = getString(R.string.error_pw_length)
                        }
                        t != pw.text.toString() -> {
                            pwCheckOutlinedTextField.error = getString(R.string.error_pw_not_equal)
                        }
                        else -> {
                            pwCheckOutlinedTextField.error = null
                        }
                    }
                }
//                onFocusChangeListener = listener
            }
            name = nameTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.isEmpty()) {
                        nameOutlinedTextField.error = getString(R.string.error_empty)
                    } else {
                        nameOutlinedTextField.error = null
                    }
                }
//                onFocusChangeListener = listener
            }
            age = ageTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.isEmpty()) {
                        ageOutlinedTextField.error = getString(R.string.error_empty)
                    } else {
                        ageOutlinedTextField.error = null
                    }
                }
//                onFocusChangeListener = listener
            }
            phone = phoneTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.isEmpty()) {
                        phoneOutlinedTextField.error = getString(R.string.error_empty)
                    } else {
                        phoneOutlinedTextField.error = null
                    }
                }
//                onFocusChangeListener = listener
            }
            signUp = signUpButton

        }

    }

    // 생명주기 2
    // 로그인 하였다면 Menu 로 이동
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }

    // 회원가입 버튼을 누르면
    fun signUp(view: View) {
        binding.apply {
            if (!emailOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
            if (!pwOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
            if (!pwCheckOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
            if (!nameOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
            if (!ageOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
            if (!phoneOutlinedTextField.error.isNullOrEmpty()) {
                emailTextInputEditText.requestFocus()
                return
            }
        }
        // 다 기입이 된 경우 이제 회원가입 요청을 합니다.
        auth.createUserWithEmailAndPassword(email.text.toString(), pw.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공 시
                    // fireStore 에 저장
                    val user = User(
                        email = email.text.toString(),
                        pw = pw.text.toString(),
                        age = age.text.toString().toInt(),
                        name = name.text.toString(),
                        phone = phone.text.toString(),
                        isManager = false,
                        date = Timestamp.now(),
                    )
                    val updateProfile = userProfileChangeRequest {
                        displayName = user.name
                    }
                    task.result!!.user!!.updateProfile(updateProfile)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("SignUpActivity", "프로필 업데이트")
                            }
                        }
                    fb.collection("users") // 컬렉션
                        .document("${email.text}") // 문서
                        .set(user)
                        .addOnSuccessListener {
                            // 전역 User 설정
                            fb.collection("users")
                                .document("${auth.currentUser!!.email}")
                                .get()
                                .addOnSuccessListener { doc ->
                                }
                            MaterialAlertDialogBuilder(this@SignUpActivity)
                                .setMessage("축하합니다! ${name.text}님! 이제 캠핑장 정보를 마음껏 이용하실 수 있습니다! ")
                                .setPositiveButton("확인") { _, _ ->
                                    startActivity(Intent(this@SignUpActivity, Menu::class.java))
                                    finishAffinity()
                                }
                                .show()
                        }
                        .addOnFailureListener { e ->
                            MaterialAlertDialogBuilder(this@SignUpActivity)
                                .setMessage("죄송합니다. 현재 회원가입이 불가능 합니다. ${e.message}")
                                .setPositiveButton("확인") { _, _ ->
                                    auth.currentUser?.delete()
                                    finish()
                                }
                                .show()
                        }
                } else {
                    MaterialAlertDialogBuilder(this@SignUpActivity)
                        .setTitle("회원가입이 불가능 합니다.")
                        .setMessage("${task.exception?.message}")
                        .setPositiveButton("확인") { _, _ ->
                        }
                        .show()
                }
            }
    }
}