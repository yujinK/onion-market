package com.yujin.onionmarket

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

class AccountFragment : Fragment(R.layout.fragment_account) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view);
    }

    private fun init(view: View) {
        val viewProfile = view.findViewById<AppCompatButton>(R.id.btn_view_profile)
        viewProfile.setOnClickListener {

        }
    }
}