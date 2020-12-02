package com.yujin.onionmarket

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
    private lateinit var locationView: LocationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
//        initSpinner(view)
        initLocationView(view)
    }

    private fun initSwipeRefreshLayout(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun initRecyclerView(view: View) {
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

//    private fun initSpinner(view: View) {
//        val spinner = view.findViewById<Spinner>(R.id.spin_location)
//        ArrayAdapter.createFromResource(
//                requireContext(),
//                R.array.location_array,
//                R.layout.view_spinner_location
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        }
//    }

    private fun initLocationView(view: View) {
        locationView = view.findViewById(R.id.location)
        locationView.setLocation("명동")
        locationView.setOnClickListener { v ->
            setDropDown()
        }
    }

    // Toolbar 지역
    private fun setDropDown() {
        if (!locationView.isOpen) {
            // 메뉴 Open
            locationView.setOpen()
        } else {
            // 메뉴 Close
            locationView.setClose()
        }
    }

    private fun refresh() {
        Toast.makeText(requireContext(), "refresh!!!", Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing = false
    }
}