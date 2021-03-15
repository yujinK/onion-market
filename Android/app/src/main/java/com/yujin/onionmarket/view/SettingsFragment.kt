package com.yujin.onionmarket.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val logout = requireView().findViewById<Button>(R.id.btn_logout)
        logout.setOnClickListener { logout() }
    }

    private fun logout() {
        Util.clearUserInfo(requireActivity())
    }
}