package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("auth/isSignUp")
    fun requestIsSignUp(
        @Query("email") email: String
    ) : Call<IsSignUpResponse>

    @FormUrlEncoded
    @POST("auth/signup")
    fun requestSignUp(
        @Field("email") email: String,
        @Field("nick") nick: String,
        @Field("password") password: String,
        @Field("locationId") locationId: Int
    ) : Call<UserResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun requestLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<UserResponse>

    @GET("category")
    fun requestCategory() : Call<CategoryResponse>

    @GET("location")
    fun requestLocation(
    ) : Call<List<Location>>
}