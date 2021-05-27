package com.jschoi.develop.aop_part03_chapter06.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jschoi.develop.aop_part03_chapter06.R
import com.jschoi.develop.aop_part03_chapter06.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment(R.layout.fragment_my_page) {

    private lateinit var binding: FragmentMyPageBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyPageBinding.bind(view)

        binding.signInOutButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            // 로그인
            if (auth.currentUser == null) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            successSignIn()
                        } else {
                            showToastMessage("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.")
                        }
                    }
            } else {
                auth.signOut()
                successSignOut()
            }
        }
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            // 회원가입
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        showToastMessage("회원가입에 성공했습니다. 로그인 버튼을 눌러주세요")
                    } else {
                        showToastMessage("회원가입에 실패했습니다. 이미 가입되어있는 이메일일 수 있습니다.")
                    }
                }

        }
        binding.emailEditText.addTextChangedListener {
            setEditTextEnable()
        }
        binding.passwordEditText.addTextChangedListener {
            setEditTextEnable()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            successSignOut()
        } else {
            binding.emailEditText.setText(auth.currentUser?.email)
            binding.passwordEditText.setText("*********")
            successSignIn()
        }
    }

    /**
     * 로그인 처리
     */
    private fun successSignIn() {
        if (auth.currentUser == null) {
            showToastMessage("로그인에 실패했습니다. 다시 시도해주세요")
            return
        }
        binding.emailEditText.isEnabled = false
        binding.passwordEditText.isEnabled = false
        binding.signUpButton.isEnabled = false
        binding.signInOutButton.text = "로그아웃"
    }

    /**
     *  로그아웃 처리
     */
    private fun successSignOut() {
        binding.emailEditText.run {
            text.clear()
            isEnabled = true
        }
        binding.passwordEditText.run {
            text.clear()
            isEnabled = true
        }
        binding.signInOutButton.run {
            text = "로그인"
            isEnabled = false
        }
        binding.signUpButton.isEnabled = false
    }

    private fun setEditTextEnable() {
        val enable =
            binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotBlank()
        binding.signInOutButton.isEnabled = enable
        binding.signUpButton.isEnabled = enable
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}