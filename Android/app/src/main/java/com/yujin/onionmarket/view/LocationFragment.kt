package com.yujin.onionmarket.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.data.Location
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LocationFragment : Fragment(R.layout.fragment_location) {
    private lateinit var retrofit: Retrofit
    private lateinit var locationService: RetrofitService

    private lateinit var locationList: List<Location>
    private lateinit var locationAdapter: LocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRetrofit()
        initSearch()
        getAllLocation()
        initRecyclerView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        locationService = retrofit.create(RetrofitService::class.java)
    }

    private fun initSearch() {
        val search = requireView().findViewById<EditText>(R.id.et_search)
        search.addTextChangedListener(DynamicTextWatcher(
            afterChanged = { s ->
                filter(s.toString())
            }
        ))
    }

    private fun initRecyclerView() {
        val rvLocation = requireView().findViewById<RecyclerView>(R.id.rv_location)
        locationAdapter = LocationAdapter(mutableListOf())
        rvLocation.adapter = locationAdapter
    }

    private fun getAllLocation() {
        val callLocation = locationService.requestLocation()
        callLocation.enqueue(object: Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                 if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                     locationList = response.body()!!
                     Log.d("onResponse()", response?.body()?.get(0)?.id.toString())
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Log.e("LocationFragment", "getAllLocation() / $t")
            }
        })
    }

    // 지역 검색 filter
    private fun filter(keyword: String) {
        var filteredList = mutableListOf<Location>()
        Log.d("filter()", keyword + "/" + locationList.size.toString())

        if (keyword.isNotBlank()) {
            for (location in locationList) {
                // 일치하는 부분이 있으면
                if (location.sido.contains(keyword) || location.sigun.contains(keyword) || location.dongmyeon.contains(keyword) || location.li.contains(keyword)) {
                    filteredList.add(location)
                }
            }
        }

        locationAdapter.filterList(filteredList)
    }

//    private fun searchLocation(keyword: String) {
//        val callLocation = locationService.requestLocation()
//        callLocation.enqueue(object: Callback<LocationResponse> {
//            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
//                if (response.isSuccessful &&  response.code() == ResponseCode.SUCCESS_GET) {
//                    Log.d("onResponse()", response.body()!!.location[0].toString())
//                }
//            }
//
//            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
//                Log.e("LocationFragment", "initRetrofit() / $t")
//            }
//        })
//    }

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
    private fun getLocation() {

    }

    private fun signUp(email: String?, nick: String?, password: String?, location: Int) {
        if (!email.isNullOrEmpty() && !nick.isNullOrEmpty() && !password.isNullOrEmpty()) {

        }
    }

    class DynamicTextWatcher (
        private val afterChanged: ((Editable?) -> Unit) = {},
        private val beforeChanged: ((CharSequence?, Int, Int, Int) -> Unit) = { _, _, _, _ -> },
        private val onChanged: ((CharSequence?, Int, Int, Int) -> Unit) = { _, _, _, _, -> }
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterChanged(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChanged(s, start, before, count)
        }
    }

    class LocationAdapter(private var locationSet: List<Location>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val location: TextView = view.findViewById(R.id.tv_location)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.location.text = "${locationSet[position].sido} ${locationSet[position].sigun} ${locationSet[position].dongmyeon} ${locationSet[position].li}"
        }

        override fun getItemCount(): Int = locationSet.size

        fun filterList(filteredList: List<Location>) {
            locationSet = filteredList
            notifyDataSetChanged()
        }
    }
}