package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.FavoriteResponse
import retrofit2.Call
import retrofit2.http.*

interface FavoriteService {
    @GET("favorite")
    fun getFavorite(@Header("authorization") token: String,
                    @Query("saleId") saleId: Int,
                    @Query("userId") userId: Int)
    : Call<FavoriteResponse>

    @FormUrlEncoded
    @POST("favorite")
    fun addFavorite(@Header("authorization") token: String,
                    @Field("saleId") saleId: Int,
                    @Field("userId") userId: Int)
    : Call<Void>

    @FormUrlEncoded
    @HTTP(method = "DELETE", hasBody = true, path = "favorite")
    fun deleteFavorite(@Header("authorization") token: String,
                       @Field("saleId") saleId: Int,
                       @Field("userId") userId: Int)
    : Call<Void>
}