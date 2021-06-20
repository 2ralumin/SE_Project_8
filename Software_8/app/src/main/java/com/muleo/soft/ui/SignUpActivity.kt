package com.muleo.soft.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.muleo.soft.CampApp
import com.muleo.soft.R
import com.muleo.soft.control.UserInfoControl
import com.muleo.soft.control.UserViewModelFactory
import com.muleo.soft.databinding.ActivitySignUpBinding
import com.muleo.soft.entity.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var fb: FirebaseFirestore

    private val uic: UserInfoControl by viewModels {
        UserViewModelFactory((application as CampApp).userRep)
    }
    private lateinit var id: EditText
    private lateinit var pw: EditText
    private lateinit var pwCheck: EditText
    private lateinit var name: EditText
    private lateinit var age: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var idMatch: Button
    private lateinit var signUp: Button

    private lateinit var binding: ActivitySignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        binding.apply {
            id = idTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.length !in 6..10) {
                        idMatch.isClickable = false
                        idOutlinedTextField.error = getString(R.string.error_id_length)
                    } else {
                        idMatch.isClickable = true
                        idOutlinedTextField.error = null
                    }
                }
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
            }
            email = emailTextInputEditText.apply {
                doAfterTextChanged {
                    val t = this.text.toString()
                    if (t.isEmpty()) {
                        emailOutlinedTextField.error = getString(R.string.error_empty)
                    } else if (!t.contains('@')) {
                        emailOutlinedTextField.error = getString(R.string.error_format)
                    } else {
                        emailOutlinedTextField.error = null
                    }
                }
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
            }
            idMatch = idMatchButton.apply { isClickable = false }

            signUp = signUpButton
            signUp.setOnClickListener {
                if (!uic.canSignUp) {
                    showIdMatch("중복확인이 필요합니다.")
                } else if (idOutlinedTextField.error.isNullOrEmpty()
                    && pwOutlinedTextField.error.isNullOrEmpty()
                    && pwCheckOutlinedTextField.error.isNullOrEmpty()
                    && nameOutlinedTextField.error.isNullOrEmpty()
                    && ageOutlinedTextField.error.isNullOrEmpty()
                    && emailOutlinedTextField.error.isNullOrEmpty()
                    && phoneOutlinedTextField.error.isNullOrEmpty()
                ) {
                    showComplete()
                } else {
                    showEmpty()
                }
            }

            fb = (application as CampApp).fb.getFirestore()!!
        }

        idMatch.setOnClickListener {
            uic.get(id.text.toString())
        }
        uic.checkUser.observe(this, {
            if (it != null) {
                uic.canSignUp = false
                binding.idOutlinedTextField.error = getString(R.string.check_id_fail)
                showIdMatch("불가")
            } else {
                uic.canSignUp = true
                showIdMatch("사용 가능합니다!")
            }
        })
        setContentView(binding.root)
    }

    fun showComplete() {


        val user = User(
            id = id.text.toString(),
            pw = pw.text.toString(),
            age = age.text.toString().toInt(),
            email = email.text.toString(),
            phone = phone.text.toString()
        )

        val dataToSave = hashMapOf(
            "id" to user.id,
            "pw" to user.pw,
            "age" to user.age,
            "email" to user.email,
            "phone" to user.phone
        )
        // Add a new document with a generated ID
        fb.collection("users")
            .add(dataToSave)
            .addOnSuccessListener { documentReference ->
                MaterialAlertDialogBuilder(this)
                    .setMessage("축하합니다! ${id.text}님! 이제 캠핑장 정보를 마음껏 이용하실 수 있습니다! $documentReference")
                    .setPositiveButton("확인") { _, _ ->
                        campInfoActivity(isUser = true)
                    }
                    .show()
            }
            .addOnFailureListener { e ->
                MaterialAlertDialogBuilder(this)
                    .setMessage("죄송합니다. 현재 회원가입이 불가능 합니다. $e")
                    .setPositiveButton("확인") { _, _ ->

                    }
                    .show()
            }


//        이 코드는 user를 DB에 저장하는 코드입니다.
//        (application as CampApp).user = user
//        uic.add(user)
//        MaterialAlertDialogBuilder(this)
//            .setMessage("축하합니다! ${id.text}님! 이제 캠핑장 정보를 마음껏 이용하실 수 있습니다!")
//            .setPositiveButton("확인") { _, _ ->
//                campInfoActivity(isUser = true)
//            }
//            .show()
    }

    fun showEmpty() {
        MaterialAlertDialogBuilder(this)
            .setMessage("채우지 않은 정보가 있습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .show()
    }

    fun showIdMatch(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun campInfoActivity(isUser: Boolean) {
        (application as CampApp).isUser = isUser
        startActivity(Intent(this, Menu::class.java))
        finishAffinity()
    }

}