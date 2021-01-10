package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("auth/isSignUp")
    fun requestIsSignUp(
        @Query("email") email: String
    ) : Call<Void>

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

    @GET("location")
    fun requestLocation() : Call<List<Location>>

    @GET("category")
    fun requestCategory(@Header("authorization") token: String) : Call<CategoryResponse>

    @FormUrlEncoded
    @POST("sale/write")
    fun requestWriteSale(@Header("authorization") token: String,
                         @Field("title") title: String,
                         @Field("content") content: String,
                         @Field("price") price: Int,
                         @Field("priceProposal") priceProposal: Int,
                         @Field("writer") writer: Int,
                         @Field("categoryId") categoryId: Int)
    : Call<WriteSaleResponse>

    @Multipart
    @POST("sale/write/image")
    fun requestWriteSaleImage(@Header("authorization") token: String,
                              @Query("saleId") saleId: Int,
                              @Part image: List<MultipartBody.Part>,
                              @Part("img") name: RequestBody)
            : Call<Void>

//    @Multipart
//    @POST("sale/write/image")
//    fun requestWriteSaleImage(@Header("authorization") token: String,
//                              @Query("saleId") saleId: Int,
//                              @Query("priority") priority: Int,
//                              @Part image: MultipartBody.Part,
//                              @Part("img") name: RequestBody)
//    : Call<EmptyResponse>
}