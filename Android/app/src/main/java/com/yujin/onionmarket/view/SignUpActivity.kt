package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.data.UserResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var signUpService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
    }

    private fun init() {
        initView()
        initRetrofit()
    }

    private fun initView() {
        val btnSignUp = findViewById<AppCompatButton>(R.id.btn_sign_up)
        btnSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        signUpService = retrofit.create(RetrofitService::class.java)
    }

    // 회원가입
    private fun signUp() {
        val email = findViewById<TextInputEditText>(R.id.et_email).text.toString()
        val nick = findViewById<TextInputEditText>(R.id.et_nick).text.toString()
        val password = findViewById<TextInputEditText>(R.id.et_password).text.toString()

        val callUser = signUpService.requestSignUp(email, nick, password)
        callUser.enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    finishSignUp()
                } else {
                    Log.d("SignUp", "[onResponse] 실패 : ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("SignUP", "[onFailure] 실패 : $t")
            }
        })
    }

    private fun finishSignUp() {
        Toast.makeText(this, getString(R.string.signup_finish), Toast.LENGTH_SHORT).show()
        finish()
    }
}