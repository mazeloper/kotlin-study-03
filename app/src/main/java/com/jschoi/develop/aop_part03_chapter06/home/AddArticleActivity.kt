package com.jschoi.develop.aop_part03_chapter06.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.DB_ARTICLES
import com.jschoi.develop.aop_part03_chapter06.databinding.ActivityAddArticleBinding

class AddArticleActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    private var selectedUri: Uri? = null
    private lateinit var binding: ActivityAddArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미지 등록 버튼
        binding.imageAddButton.setOnClickListener {
            when {
                // 퍼미션 체크
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                // 교육용 퍼미
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }

        // 등록하기 버튼
        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val price = binding.priceEditText.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            showProgress()
            // 중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val image = selectedUri ?: return@setOnClickListener

                uploadPhoto(image,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                uploadArticle(sellerId, title, price, "")
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) { // 업로드 성공
                    // 다운로드 URL 가져오기
                    storage.reference.child("article/photo").child(fileName).downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                            hideProgress()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "${price}원", imageUrl)
        articleDB.push().setValue(model)

        hideProgress()
        finish()
    }

    @Suppress("DEPRECATION")
    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
            .create()
            .show()
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            2000 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.photoImageView.setImageURI(uri)
                    selectedUri = uri
                }

            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContentProvider()
            } else {
                Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}