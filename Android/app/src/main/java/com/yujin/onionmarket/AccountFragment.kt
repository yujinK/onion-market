package com.yujin.onionmarket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class AccountFragment : Fragment(R.layout.fragment_account) {
    private var isLoggedIn = false;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view);
    }

    private fun init(view: View) {
        val container = view.findViewById<ConstraintLayout>(R.id.profile_container)
        if (!isLoggedIn) {
            //로그인하기 layout 추가
            LayoutInflater.from(requireContext()).inflate(R.layout.view_login, container)
        } else {
            //사용자 프로필 layout 추가
        }
        container.setOnClickListener {
            if (!isLoggedIn) {
                //로그인하기
                login();
            }
        }
    }

    private fun login() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }
}