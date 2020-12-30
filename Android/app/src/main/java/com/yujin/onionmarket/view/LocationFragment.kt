package com.yujin.onionmarket.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.yujin.onionmarket.R

class LocationFragment : Fragment(R.layout.fragment_location) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSignUpInfo()
    }

    private fun getSignUpInfo() {
        setFragmentResultListener("requestInfo") { requestKey, bundle ->
            val email = bundle.getString("email")
            val nick = bundle.getString("nick")
            val password = bundle.getString("password")

            Log.d("LocationFragment", "$email/$nick/$password")
            /*val location = getLocation()*/
//            signUp(email, nick, password, location)
        }
    }

    // 지역 검색
//    private fun getLocation() : Int {
//
//    }

    private fun signUp(email: String?, nick: String?, password: String?, location: Int) {
        if (!email.isNullOrEmpty() && !nick.isNullOrEmpty() && !password.isNullOrEmpty()) {

        }
    }
}