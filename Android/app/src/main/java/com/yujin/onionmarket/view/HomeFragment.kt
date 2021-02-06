package com.yujin.onionmarket.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yujin.onionmarket.*
import com.yujin.onionmarket.data.ReadSaleResponse
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.AuthService
import com.yujin.onionmarket.network.SaleService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var retrofit: Retrofit
    private lateinit var saleService: SaleService

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var locationView: LocationView
    private lateinit var popupWindow: PopupWindow

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SaleAdapter

    private var isOpen = false

    // 게시글 작성 후
    private val writeContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
        if (result?.resultCode == RESULT_OK) {
            readSales()
        }
    }

    private val loginContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
        if (result?.resultCode == RESULT_OK) {
            init(requireView())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initLocationView(view)
        initFAB(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        saleService = retrofit.create(SaleService::class.java)
    }

    private fun initSwipeRefreshLayout(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rv_sale)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)
        readSales()
    }

    private fun initLocationView(view: View) {
        locationView = view.findViewById(R.id.location)
        val user = Util.readUser(requireActivity())
        if (user != null) {
            val location = Util.readUser(requireActivity())?.location?.dongmyeon
            locationView.setLocation(location)
            locationView.setOnClickListener {
                setDropDown(location)
                isOpen = !isOpen
            }
        }
    }

    private fun initFAB(view: View) {
        val writeSale = view.findViewById<FloatingActionButton>(R.id.btn_write_sale)
        writeSale.setOnClickListener {
            val user = Util.readUser(requireActivity())
            if (user == null) {
                // Login 안 한 유저
                Util.requireLogin(requireContext()) { _, _ -> moveLogin() }
            } else {
                // Login 한 유저
                moveWriteSale()
            }
        }
    }

    // 로그인으로 이동
    private fun moveLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        loginContract.launch(intent)
    }

    private fun readSales() {
        val token = Util.readToken(requireActivity())
        val locationId = Util.readUser(requireActivity())?.location?.id
        if (token != "" && locationId != null) {
            val callSales = saleService.readSaleWithLocation(token, locationId, 0)
            callSales.enqueue(object : Callback<ReadSaleResponse> {
                override fun onResponse(call: Call<ReadSaleResponse>, response: Response<ReadSaleResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                        val sales = response.body()?.sales
                        setSaleAdapter(sales)
                    }
                }

                override fun onFailure(call: Call<ReadSaleResponse>, t: Throwable) {
                    Log.e("HomeFragment", "readSales() / $t")
                }
            })
        }
    }

    private fun setSaleAdapter(sales: ArrayList<Sale>?) {
        if (sales != null) {
            adapter = SaleAdapter(requireContext(), sales, 0)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    // 글쓰기 Activity 이동
    private fun moveWriteSale() {
        val intent = Intent(activity, WriteActivity::class.java)
        writeContract.launch(intent)
    }

    // Toolbar 지역
    private fun setDropDown(location: String?) {
        if (!isOpen) {
            // 메뉴 Open
            open(location)
        } else {
            // 메뉴 Close
            close()
        }
    }

    private fun open(location: String?) {
        locationView.setOpen()
        setPopupWindow(location)
    }

    private fun close() {
        locationView.setClose()
        popupWindow.dismiss()
    }

    // PopupWindow 지역
    private fun setPopupWindow(location: String?) {
        if (location != null) {
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customView = inflater.inflate(R.layout.view_popup_location, null)
            customView.findViewById<TextView>(R.id.tv_popup_location).text = location
            popupWindow = PopupWindow(
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
    }

    // SwipeRefreshLayout
    private fun refresh() {
        readSales()
        swipeRefreshLayout.isRefreshing = false
    }
}