package com.muleo.soft.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muleo.soft.R
import com.muleo.soft.databinding.ActivityCampWriteBinding
import com.muleo.soft.entity.Camp
import com.muleo.soft.util.FirebaseUtil
import com.muleo.soft.util.StorageUtil
import java.io.ByteArrayOutputStream
import java.util.*

class CampWriteActivity : AppCompatActivity() {

    // Firebase
    private lateinit var fb: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // View
    private lateinit var imageView: ImageView
    private lateinit var setImage: Button
    private lateinit var name: EditText
    private lateinit var address: EditText
    private lateinit var account: EditText
    private lateinit var won: EditText
    private lateinit var info: EditText
    private lateinit var save: Button
    private lateinit var binding: ActivityCampWriteBinding

    // 핸드폰에서 이미지 선택
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        // 리턴되는 Uri 를 다루면됩니다.
        imageView.setImageURI(it)
    }

    // 생명주기 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCampWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase
        storage = StorageUtil.fireStorage
        fb = FirebaseUtil.fireStore

        // View 를 init
        binding.apply {
            imageView = campImageView
            setImage = setImageButton
            name = nameEditText
            address = addressEditText
            account = accountEditText
            won = wonEditText
            info = infoEditText
            save = saveButton
        }

        // 정보 입력 검사
        binding.apply {
            name.doAfterTextChanged {
                val t = name.text.toString()
                if (t.isEmpty()) {
                    nameOutlinedTextField.error = getString(R.string.error_length)
                } else {
                    nameOutlinedTextField.error = null
                }
            }
            address.doAfterTextChanged {
                val t = address.text.toString()
                if (t.isEmpty()) {
                    addressOutlinedTextField.error = getString(R.string.error_length)
                } else {
                    addressOutlinedTextField.error = null
                }
            }
            account.doAfterTextChanged {
                val t = account.text.toString()
                if (t.isEmpty()) {
                    accountOutlinedTextField.error = getString(R.string.error_length)
                } else {
                    accountOutlinedTextField.error = null
                }
            }
            info.doAfterTextChanged {
                val t = info.text.toString()
                if (t.isEmpty()) {
                    infoOutlinedTextField.error = getString(R.string.error_length)
                } else {
                    infoOutlinedTextField.error = null
                }
            }
            won.doAfterTextChanged {
                val t = info.text.toString()
                if (t.isEmpty()) {
                    wonOutlinedTextField.error = getString(R.string.error_length)
                } else {
                    wonOutlinedTextField.error = null
                }
            }

            // 처음에 모두 에러 표시
            nameOutlinedTextField.error = getString(R.string.error_length)
            addressOutlinedTextField.error = getString(R.string.error_length)
            accountOutlinedTextField.error = getString(R.string.error_length)
            infoOutlinedTextField.error = getString(R.string.error_length)
            wonOutlinedTextField.error = getString(R.string.error_length)
        }
    }


    // 이미지 변경 버튼을 클릭 했을 경우
    fun setImage(view: View) {
        // 갤러리에서 이미지를 가져오는 방식으로
        getContent.launch("image/*")
    }

    //저장하기 버튼을 클릭 했을 경우
    fun saveCamp(view: View) {
        //체크
        binding.apply {
            if (imageView.drawable == null) {
                Snackbar.make(view, "사진이 들어가야 합니다.", Snackbar.LENGTH_SHORT).show()
                return
            }
            if (!nameOutlinedTextField.error.isNullOrEmpty()) {
                nameOutlinedTextField.requestFocus()
                return
            }
            if (!addressOutlinedTextField.error.isNullOrEmpty()) {
                addressOutlinedTextField.requestFocus()
                return
            }
            if (!accountOutlinedTextField.error.isNullOrEmpty()) {
                accountOutlinedTextField.requestFocus()
                return
            }
            if (!infoOutlinedTextField.error.isNullOrEmpty()) {
                infoOutlinedTextField.requestFocus()
                return
            }
            if (!wonOutlinedTextField.error.isNullOrEmpty()) {
                wonOutlinedTextField.requestFocus()
                return
            }
        }


        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val storageRef = storage.reference
        val campsRef = storageRef.child("camps/${UUID.randomUUID()}.png")

        val uploadTask = campsRef.putBytes(data)
        binding.campProgressbar.visibility = View.VISIBLE
        save.isEnabled = false
        setImage.isEnabled = false
        name.isEnabled = false
        address.isEnabled = false
        account.isEnabled = false
        info.isEnabled = false
        won.isEnabled = false
        uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("CampUpdateActivity", "업로드 성공")
            } else {
                Log.d("CampUpdateActivity", "업로드 실패${it.exception}")
            }
        }

        val getDownloadUriTask = uploadTask.continueWithTask {
            if (!it.isSuccessful) {
                Toast.makeText(this, "이미지 경로 실패", Toast.LENGTH_SHORT).show()
                throw Throwable(it.exception)
            } else {
                campsRef.downloadUrl
            }
        }
        getDownloadUriTask.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val downloadUri = it.result
                // 해당 이미지 다운로드 경로를 가지고 fb에 저장
                val c = Camp(
                    id = "${UUID.randomUUID()}",
                    name = "${name.text}",
                    latitude = 0.0,
                    longitude = 0.0,
                    address = "${address.text}",
                    isEmpty = binding.toggleButton.isChecked,
                    imgPath = "$downloadUri",
                    accountNumber = "${account.text}",
                    info = "${info.text}",
                    date = Timestamp.now(),
                    won = won.text.toString().toLong(),
                )

                fb.collection("camps")
                    .document(c.id!!)
                    .set(c)
                    .addOnCompleteListener { fbtask ->
                        if (fbtask.isSuccessful) {
                            finish()
                        } else {
                            Toast.makeText(this, "업로드 실패!!", Toast.LENGTH_SHORT).show()
                        }
                        binding.campProgressbar.visibility = View.INVISIBLE
                        save.isEnabled = true
                        setImage.isEnabled = true
                        name.isEnabled = true
                        address.isEnabled = true
                        account.isEnabled = true
                        info.isEnabled = true
                        won.isEnabled = true
                    }
            } else {
                Log.d("CampUpdateActivity", "다운로드 경로 만들기 실패${it.exception}")
            }
        }
    }
}