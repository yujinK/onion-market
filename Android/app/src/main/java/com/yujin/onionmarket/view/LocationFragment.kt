package com.yujin.onionmarket.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.data.Location
import com.yujin.onionmarket.data.LocationResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LocationFragment : Fragment(R.layout.fragment_location) {
    private lateinit var retrofit: Retrofit
    private lateinit var locationService: RetrofitService

    private lateinit var allLocation: List<Location>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRetrofit()
        initSearch()
        getAllLocation()
//        val location = requireView().findViewById<AppCompatAutoCompleteTextView>(R.id.tv_location)
//        val array = arrayOf("인천", "서울")
//        adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, array)
//        location.setAdapter(adapter)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        locationService = retrofit.create(RetrofitService::class.java)
    }

    private fun initSearch() {
        val search = requireView().findViewById<EditText>(R.id.et_search)
        search.addTextChangedListener(DynamicTextWatcher(
            afterChanged = { s ->
//                searchLocation(s.toString())
            }
        ))
    }

    private fun getAllLocation() {
        val callLocation = locationService.requestLocation()
        callLocation.enqueue(object: Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                 if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
//                     Log.d("onResponse()", response.body()?.locations?.get(0)?.id.toString())
                     Log.d("onResponse()", response?.body()?.get(0)?.id.toString())
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Log.e("LocationFragment", "getAllLocation() / $t")
            }
        })
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

//    class LocationAdapter(private val locationSet: Array<Location>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
//        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val location: TextView = view.findViewById(R.id.tv_location)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
//            return ViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.location.text = locationSet[position].name
//        }
//
//        override fun getItemCount(): Int = locationSet.size
//    }
}