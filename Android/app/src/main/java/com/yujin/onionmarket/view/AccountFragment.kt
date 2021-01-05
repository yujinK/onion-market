package com.yujin.onionmarket.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.yujin.onionmarket.R
import com.yujin.onionmarket.RequestCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.User

class AccountFragment : Fragment(R.layout.fragment_account) {
    private lateinit var loginView: ConstraintLayout
    private lateinit var profileView: ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.LOGIN) {
            if (resultCode == RESULT_OK) {
                setAccount()
            }
        }
    }

    private fun init(view: View) {
        initView(view)
        setAccount()
    }

    private fun initView(view: View) {
        loginView = view.findViewById(R.id.login_container)
        loginView.setOnClickListener { moveLogin() }
        profileView = view.findViewById(R.id.profile_container)
        profileView.setOnClickListener { moveProfile() }
    }

    // 계정 setting
    private fun setAccount() {
        val user = Util.readUserInfo(requireActivity())
        if (user != null) {
            // 로그인 한 유저
            setUserProfile(user)
        } else {
            // 로그인 하지 않은 유저
            setNoUserProfile()
        }
    }

    private fun setUserProfile(user: User) {
        visibleProfileView()
        setUserInfo(user)
    }

    private fun setNoUserProfile() {
        visibleLoginView()
    }

    private fun setUserInfo(user: User) {
//        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: return
//        val nick = sharedPref.getString("nick", "")
//        val img = sharedPref.getString("img", "")
//        //TODO: location 추가
//
//        if (!img.isNullOrEmpty()) {
//            //TODO: Glide 써서 설정
//        }

        profileView.getChildAt(1).findViewById<TextView>(R.id.tv_nick).text = user.nick
        profileView.getChildAt(2).findViewById<TextView>(R.id.tv_location).text = user.location[0].dongmyeon
    }

    //로그인하기 layout visible
    private fun visibleLoginView() {
        loginView.visibility = View.VISIBLE
        profileView.visibility = View.GONE
    }

    //사용자 프로필 layout visible
    private fun visibleProfileView() {
        profileView.visibility = View.VISIBLE
        loginView.visibility = View.GONE
    }

    // 로그인으로 이동
    private fun moveLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, RequestCode.LOGIN)
    }

    // 프로필로 이동
    private fun moveProfile() {

    }
}