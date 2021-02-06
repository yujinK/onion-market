package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface LocationService {
    @GET("location")
    fun getLocation() : Call<List<Location>>
}