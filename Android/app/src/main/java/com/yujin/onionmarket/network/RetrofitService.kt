package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.CategoryResponse
import com.yujin.onionmarket.data.IsSignUpResponse
import com.yujin.onionmarket.data.UserResponse
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
        @Field("password") password: String
    ) : Call<UserResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun requestLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<UserResponse>

    @GET("category")
    fun requestCategory() : Call<CategoryResponse>
}