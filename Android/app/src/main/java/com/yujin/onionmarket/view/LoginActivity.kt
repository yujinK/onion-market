package com.yujin.onionmarket.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.data.User
import com.yujin.onionmarket.data.UserResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var loginService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        initView()
        initRetrofit()
    }

    private fun initView() {
        val btnLogin = findViewById<AppCompatButton>(R.id.btn_login)
        btnLogin.setOnClickListener { login() }

        val tvSignUp = findViewById<TextView>(R.id.tv_sign_up)
        tvSignUp.setOnClickListener { signUp() }
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        loginService = retrofit.create(RetrofitService::class.java)
    }

    // 로그인
    private fun login() {
        val email = findViewById<TextInputEditText>(R.id.et_email).text.toString()
        val password = findViewById<TextInputEditText>(R.id.et_password).text.toString()

        val callUser = loginService.requestLogin(email, password)
        callUser.enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    val user: User = response.body()!!.user[0]
                    saveUserData(user)
                    setResult(RESULT_OK)
                    finish()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("LoginActivity-login()", t.toString())
            }
        })
    }

    // 사용자 데이터 저장
    private fun saveUserData(user: User) {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("id", user.id)
            putString("nick", user.nick)
            putString("img", user.img)
            //TODO: 지역
            commit()
        }
    }

    // 회원가입 이동
    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}