package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.add
import androidx.fragment.app.commit
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
    }



    private fun init() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<SignUpInfoFragment>(R.id.container)
        }
    }
}