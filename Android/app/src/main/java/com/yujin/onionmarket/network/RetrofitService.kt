package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {
    @FormUrlEncoded
    @POST("auth/signup")
    fun requestSignUp(
        @Field("email") email: String,
        @Field("nick") nick: String,
        @Field("password") password: String
    ) : Call<UserResponse>


}