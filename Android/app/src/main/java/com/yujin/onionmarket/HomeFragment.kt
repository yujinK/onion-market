package com.yujin.onionmarket

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh() {
        Toast.makeText(requireContext(), "refresh!!!", Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing = false
    }
}