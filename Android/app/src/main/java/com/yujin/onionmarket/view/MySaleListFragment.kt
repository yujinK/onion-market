package com.yujin.onionmarket.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yujin.onionmarket.R

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
        return MySaleFragment()
    }

    override fun getItemCount(): Int = 3
}

class MySaleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_sale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}