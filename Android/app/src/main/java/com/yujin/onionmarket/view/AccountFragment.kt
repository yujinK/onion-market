package com.yujin.onionmarket.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yujin.onionmarket.R
import com.yujin.onionmarket.RequestCode

class AccountFragment : Fragment(R.layout.fragment_account) {
    private var isLoggedIn = false
    private lateinit var loginView: ConstraintLayout
    private lateinit var profileView: ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.LOGIN) {
            if (resultCode == RESULT_OK) {
                successLogin()
            }
        }
    }

    private fun init(view: View) {
        initView(view)
        setProfileView()
    }

    private fun initView(view: View) {
        loginView = view.findViewById(R.id.login_container)
        loginView.setOnClickListener { moveLogin() }
        profileView = view.findViewById(R.id.profile_container)
        profileView.setOnClickListener { moveProfile() }
    }

    private fun moveLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, RequestCode.LOGIN)
    }

    private fun moveProfile() {

    }

    private fun setProfileView() {
        if (!isLoggedIn) {
            //로그인하기 layout visible
            visibleLoginView()
        } else {
            //사용자 프로필 layout visible
            visibleProfileView()
        }
    }

    private fun successLogin() {
        isLoggedIn = true
        setProfileView()
        setAccount()
    }

    private fun setAccount() {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
        val nick = sharedPref.getString("nick", "")
        val img = sharedPref.getString("img", "")
        //TODO: location 추가

        if (!img.isNullOrEmpty()) {
            //TODO: Glide 써서 설정
        }
        profileView.getChildAt(1).findViewById<TextView>(R.id.tv_nick).text = nick
    }

    private fun visibleLoginView() {
        loginView.visibility = View.VISIBLE
        profileView.visibility = View.GONE
    }

    private fun visibleProfileView() {
        profileView.visibility = View.VISIBLE
        loginView.visibility = View.GONE
    }
}