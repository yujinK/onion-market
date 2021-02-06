package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface AuthService {
    @GET("auth/isSignUp")
    fun isSignUp(
        @Query("email") email: String
    ) : Call<Void>

    @FormUrlEncoded
    @POST("auth/signup")
    fun signUp(
        @Field("email") email: String,
        @Field("nick") nick: String,
        @Field("password") password: String,
        @Field("locationId") locationId: Int
    ) : Call<UserResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<UserResponse>
}