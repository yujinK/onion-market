package com.yujin.onionmarket.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpInfoFragment : Fragment(R.layout.fragment_sign_up_info) {
    private lateinit var retrofit: Retrofit
    private lateinit var signUpService: RetrofitService
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailView: TextInputEditText
    private lateinit var nickLayout: TextInputLayout
    private lateinit var nickView: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordView: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initRetrofit()
    }

    private fun initView() {
        initEmail()
        initNick()
        initPassword()
        initNext()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        signUpService = retrofit.create(RetrofitService::class.java)
    }

    private fun initEmail() {
        emailLayout = requireView().findViewById(R.id.til_email)
        emailView = requireView().findViewById(R.id.et_email)
        emailView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
    }

    private fun initNick() {
        nickLayout = requireView().findViewById(R.id.til_nick)
        nickView = requireView().findViewById(R.id.et_nick)
        nickView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (nickView.text.isNullOrEmpty()) {
                    nickLayout.error = getString(R.string.require_input)
                } else {
                    nickLayout.error = null
                }
            }
        }
    }

    private fun initPassword() {
        passwordLayout = requireView().findViewById(R.id.til_password)
        passwordView = requireView().findViewById(R.id.et_password)
        passwordView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (passwordView.text.isNullOrBlank()) {
                    passwordLayout.error = getString(R.string.require_input)
                } else {
                    passwordLayout.error = null
                }
            }
        }
    }

    private fun initNext() {
        val btnNext = requireView().findViewById<AppCompatButton>(R.id.btn_next)
        btnNext.setOnClickListener {
            if (!emailView.text.isNullOrEmpty() && !nickView.text.isNullOrEmpty() && !passwordView.text.isNullOrEmpty()) {
                checkUser()
            } else {
                setError()
            }
        }
    }

    private fun setError() {
        if (emailView.text.isNullOrEmpty()) {
            emailLayout.error = getString(R.string.require_input)
        }
        if (nickView.text.isNullOrEmpty()) {
            nickLayout.error = getString(R.string.require_input)
        }
        if (passwordView.text.isNullOrEmpty()) {
            passwordLayout.error = getString(R.string.require_input)
        }
    }

    // 지역 검색으로 이동
    private fun checkUser() {
        val email = emailView.text.toString()
        val nick = nickView.text.toString()
        val password = passwordView.text.toString()

        val callUser = signUpService.isSignUp(email)
        callUser.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        ResponseCode.SUCCESS_GET -> {
                            // 가입 가능 User
                            emailLayout.error = null
                            moveNext(email, nick, password)
                        }
                    }
                } else {
                    // 이미 존재하는 User
                    emailLayout.error = getString(R.string.fail_email_message)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("SignUpInfo", "[onFailure] 실패 : $t")
            }
        })
    }

    private fun moveNext(email: String, nick: String, password: String) {
        parentFragmentManager.commit {
            // 유저 정보 전달
            val bundle = Bundle()
            bundle.putString("email", email)
            bundle.putString("nick", nick)
            bundle.putString("password", password)
            setFragmentResult("requestInfo", bundle)

            setReorderingAllowed(true)
            add<LocationFragment>(R.id.user_container)
        }
    }

    private fun finishSignUp() {
        Toast.makeText(requireContext(), getString(R.string.signup_finish), Toast.LENGTH_SHORT).show()
//        finish()
    }

    private fun validateEmail() : Boolean {
        return if (emailView.text.isNullOrEmpty()) {
            emailLayout.error = getString(R.string.require_input)
            false
        } else {
            val email = emailView.text.toString()
            val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isValid) {
                emailLayout.error = getString(R.string.invalid_email)
                false
            } else {
                emailLayout.error = null
                true
            }
        }
    }
}