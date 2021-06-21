package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.databinding.ActivityLoginBinding
import com.muleo.soft.util.AuthUtil
import com.muleo.soft.util.FirebaseUtil

class LoginActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // View
    private lateinit var email: EditText
    private lateinit var pw: EditText
    private lateinit var login: Button
    private lateinit var signUp: Button
    private lateinit var binding: ActivityLoginBinding

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        // View 를 init
        binding.apply {
            // 구성 요소
            email = emailTextInputEditText
            pw = pwTextInputEditText
            login = loginButton
            signUp = singUpButton
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
    fun signUpActivity(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    // 로그인 버튼을 누르면
    fun login(view: View) {
        auth.signInWithEmailAndPassword(email.text.toString(), pw.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 전역 User 설정
                    fb.collection("users")
                        .document("${auth.currentUser!!.email}")
                        .get()
                        .addOnSuccessListener { doc ->
                        }
                    // 로그인 성공 시
                    startActivity(Intent(this, Menu::class.java))
                    finish()
                } else {
                    // 로그인 실패 시
                    Toast.makeText(
                        baseContext, "로그인 실패 ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}