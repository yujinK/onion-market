package com.yujin.onionmarket.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Sale
import com.yujin.onionmarket.SaleAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var locationView: LocationView
    private lateinit var popupWindow: PopupWindow

    private var isOpen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initLocationView(view)
        initFAB(view)
    }

    private fun initSwipeRefreshLayout(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun initRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_sale)
        val array = arrayOf(Sale("aa"), Sale("bb"), Sale("cc"), Sale("dd"))
        val adapter = SaleAdapter(array)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initLocationView(view: View) {
        locationView = view.findViewById(R.id.location)
        locationView.setLocation("명동")
        locationView.setOnClickListener {
            setDropDown()
            isOpen = !isOpen
        }
    }

    private fun initFAB(view: View) {
        val writeSale = view.findViewById<FloatingActionButton>(R.id.btn_write_sale)
        writeSale.setOnClickListener { moveWriteSale() }
    }

    // 글쓰기 Activity 이동
    private fun moveWriteSale() {
        val intent = Intent(activity, WriteActivity::class.java)
        startActivity(intent)
    }

    // Toolbar 지역
    private fun setDropDown() {
        if (!isOpen) {
            // 메뉴 Open
            open()
        } else {
            // 메뉴 Close
            close()
        }
    }

    private fun open() {
        locationView.setOpen()
        setPopupWindow()
    }

    private fun close() {
        locationView.setClose()
        popupWindow.dismiss()
    }

    // PopupWindow 지역
    private fun setPopupWindow() {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.view_popup_location, null)
        popupWindow  = PopupWindow(
                customView,
                600,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.elevation = 10.0f
        popupWindow.showAsDropDown(locationView, 0, -30)
        popupWindow.setTouchInterceptor { v, event ->
            when (event.action) {
                MotionEvent.ACTION_OUTSIDE -> {
                    close()
                    true
                }
                else -> false
            }
        }
        popupWindow.isOutsideTouchable = true
    }

    // SwipeRefreshLayout
    private fun refresh() {
        Toast.makeText(requireContext(), "refresh!!!", Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing = false
    }
}