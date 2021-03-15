package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.yujin.onionmarket.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
    }

    private fun init() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<SignUpInfoFragment>(R.id.user_container)
        }
    }
}