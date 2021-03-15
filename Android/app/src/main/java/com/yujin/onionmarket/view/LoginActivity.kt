package com.yujin.onionmarket.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.User
import com.yujin.onionmarket.data.UserResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.AuthService
import com.yujin.onionmarket.network.FcmService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var loginService: AuthService
    private lateinit var fcmService: FcmService

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
        loginService = retrofit.create(AuthService::class.java)
        fcmService = retrofit.create(FcmService::class.java)
    }

    // 로그인
    private fun login() {
        val email = findViewById<TextInputEditText>(R.id.et_email).text.toString()
        val password = findViewById<TextInputEditText>(R.id.et_password).text.toString()

        val callUser = loginService.login(email, password)
        callUser.enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    val user: User = response.body()!!.user[0]
                    val token: String = response.body()!!.token
                    Log.d(TAG, "login(): ${user.toString()}, token: $token")
                    successLogin(user, token)
                } else {
                    failLogin()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "login()-onFailure(): ${t}")
            }
        })
    }

    // 로그인 성공
    private fun successLogin(user: User, token: String) {
        sendFCMRegistration(user, token)
        saveUserData(user, token)
        setResult(RESULT_OK)
        finish()
    }

    // 로그인 실패
    private fun failLogin() {
        MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.fail_login_message))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    
                }
                .show()
    }

    // Firebase Token 저장
    private fun sendFCMRegistration(user: User, userToken: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result!!
                val callFcm = fcmService.sendRegistration(userToken, user.id, fcmToken)
                callFcm.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d(TAG, "saveFcmToken()-onResponse: $fcmToken")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e(TAG, "saveFcmToken()-onFailure(): $t")
                    }
                })
            }
        }
    }

    // 사용자 데이터 저장
    private fun saveUserData(user: User, token: String) {
        Util.saveUserInfo(this, user, token)
    }

    // 회원가입 이동
    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}