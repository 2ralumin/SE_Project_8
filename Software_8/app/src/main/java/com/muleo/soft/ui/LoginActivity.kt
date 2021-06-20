package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.muleo.soft.CampApp
import com.muleo.soft.R
import com.muleo.soft.control.UserInfoControl
import com.muleo.soft.control.UserViewModelFactory
import com.muleo.soft.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val uic: UserInfoControl by viewModels {
        UserViewModelFactory((application as CampApp).userRep)
    }
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var login: Button
    private lateinit var signUp: Button
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.apply {
            // 구성 요소
            id = idTextInputEditText
            pw = pwTextInputEditText
            login = loginButton
            signUp = singUpButton
        }

        login.setOnClickListener {
            uic.get(id.text.toString())
        }
        uic.checkUser.observe(this, {
            if (it != null) {
                if (it.pw == pw.text.toString()) {
                    (application as CampApp).user = it
                    if (it.id == getString(R.string.manager_id)) {
                        campInfoActivity(isUser = false)
                    } else {
                        campInfoActivity(isUser = true)
                    }

                } else {
                    showMisMatch("비밀번호 불일치")
                }
            } else {
                showMisMatch("없는 id")
            }
        })

        setContentView(binding.root)
    }

    // 회원가입 버튼을 누르면
    fun registerUser(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun showMisMatch(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun campInfoActivity(isUser: Boolean) {
        (application as CampApp).isUser = isUser
        startActivity(Intent(this, Menu::class.java))
        finish()
    }



}