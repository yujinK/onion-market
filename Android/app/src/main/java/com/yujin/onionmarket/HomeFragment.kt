package com.yujin.onionmarket

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_sale)
        val array = arrayOf(Sale("aa"), Sale("bb"), Sale("cc"))
        val adapter = SaleAdapter(array)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun refresh() {
        Toast.makeText(requireContext(), "refresh!!!", Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing = false
    }
}