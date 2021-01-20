package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
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

    @GET("location")
    fun getLocation() : Call<List<Location>>

    @GET("category")
    fun getCategory(@Header("authorization") token: String) : Call<CategoryResponse>

    @FormUrlEncoded
    @POST("sale/write")
    fun writeSale(@Header("authorization") token: String,
                  @Field("title") title: String,
                  @Field("content") content: String,
                  @Field("price") price: Int,
                  @Field("priceProposal") priceProposal: Int,
                  @Field("writer") writer: Int,
                  @Field("categoryId") categoryId: Int)
    : Call<WriteSaleResponse>

    @Multipart
    @POST("sale/write/image")
    fun writeSaleImage(@Header("authorization") token: String,
                       @Query("saleId") saleId: Int,
                       @Part image: List<MultipartBody.Part>,
                       @Part("img") name: RequestBody)
    : Call<Void>

    // state : 0(판매중), 1(거래완료), 2(숨김)
    @GET("sale/location/{locationId}")
    fun readSaleWithLocation(@Header("authorization") token: String,
                             @Path("locationId") locationId: Int,
                             @Query("state") state: Int)
    : Call<ReadSaleResponse>

    @GET("sale/user/{userId}")
    fun readSaleWithUser(@Header("authorization") token: String,
                         @Path("userId") userId: Int,
                         @Query("state") state: Int)
    : Call<ReadSaleResponse>

    @FormUrlEncoded
    @POST("sale/delete")
    fun deleteSale(@Header("authorization") token: String,
                   @Field("id") id: Int)
    : Call<Void>

    @FormUrlEncoded
    @POST("sale/delete/image")
    fun deleteSaleImage(@Header("authorization") token: String,
                        @Field("path") path: String)
    : Call<Void>

    @FormUrlEncoded
    @POST("sale/edit")
    fun editSale(@Header("authorization") token: String,
                 @Field("id") id: Int,
                 @Field("title") title: String,
                 @Field("content") content: String,
                 @Field("price") price: Int,
                 @Field("priceProposal") priceProposal: Int,
                 @Field("categoryId") categoryId: Int)
    : Call<WriteSaleResponse>
}