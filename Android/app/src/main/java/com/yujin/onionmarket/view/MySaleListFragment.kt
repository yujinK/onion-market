package com.yujin.onionmarket.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.SaleAdapter
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.ReadSaleResponse
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MySaleListFragment : Fragment() {
    private lateinit var saleListAdapter: SaleListAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_sale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saleListAdapter = SaleListAdapter(this)
        viewPager = view.findViewById(R.id.pager_my_sale)
        viewPager.adapter = saleListAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_my_sale)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.sale_ing)
                1 -> tab.text = getString(R.string.sale_done)
                2 -> tab.text = getString(R.string.sale_hide)
            }
        }.attach()
    }
}

class SaleListAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return MySaleFragment(position)
    }

    override fun getItemCount(): Int = 3
}

class MySaleFragment(private val position: Int) : Fragment() {
    private lateinit var retrofit: Retrofit
    private lateinit var saleService: RetrofitService

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initRetrofit()
        return inflater.inflate(R.layout.fragment_my_sale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rv_sale)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)
        readSale()
    }

    override fun onResume() {
        super.onResume()
        readSale()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        saleService = retrofit.create(RetrofitService::class.java)
    }

    private fun readSale() {
        val token = Util.readToken(requireActivity())
        if (token != "") {
            val userId = Util.readUser(requireActivity())!!.id
            val state = position
            val callSales = saleService.readSaleWithUser(token, userId, state)
            callSales.enqueue(object: Callback<ReadSaleResponse> {
                override fun onResponse(call: Call<ReadSaleResponse>, response: Response<ReadSaleResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                        val sales = response.body()!!.sales
                        setSaleAdapter(sales)
                    }
                }

                override fun onFailure(call: Call<ReadSaleResponse>, t: Throwable) {
                    Log.e("MySaleFragment", "readSale() / $t")
                }
            })
        }
    }

    private fun setSaleAdapter(sales: List<Sale>) {
        val adapter = SaleAdapter(requireContext(), sales, 1)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}