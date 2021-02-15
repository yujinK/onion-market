package com.yujin.onionmarket.network

import retrofit2.Call
import retrofit2.http.*

interface FcmService {
    @FormUrlEncoded
    @POST("fcm/{userId}")
    fun sendRegistration(@Header("authorization") token: String,
                         @Path("userId") userId: Int,
                         @Field("fcm") fcm: String)
    : Call<Void>
}