package com.yujin.onionmarket.network

import com.yujin.onionmarket.data.*
import retrofit2.Call
import retrofit2.http.*

interface ChatService {
    @GET("chat/existing-sale-chat")
    fun existingSaleChat(@Header("authorization") token: String,
                         @Query("saleId") saleId: Int)
    : Call<ChatsResponse>

    @GET("chat/existing-buy-chat")
    fun existingBuyChat(@Header("authorization") token: String,
                        @Query("saleId") saleId: Int,
                        @Query("userId") userId: Int)
    : Call<ChatIdResponse>

    @GET("chat/user/{userId}")
    fun loadUserChat(@Header("authorization") token: String,
                     @Path("userId") userId: Int)
    : Call<UserChatsResponse>

    @FormUrlEncoded
    @POST("chat/new-chat")
    fun newChat(@Header("authorization") token: String,
                @Field("buyUserId") buyUserId: Int,
                @Field("saleId") saleId: Int)
    : Call<ChatIdResponse>

    @GET("chat/load/{chatId}")
    fun loadChat(@Header("authorization") token: String,
                 @Path("chatId") chatId: Int)
    : Call<LoadChatResponse>

    @FormUrlEncoded
    @POST("chat/send/{chatId}")
    fun sendMessage(@Header("authorization") token: String,
                    @Path("chatId") chatId: Int,
                    @Field("message") message: String,
                    @Field("userId") userId: Int)
    : Call<Void>

    @DELETE("chat/delete/{chatId}")
    fun deleteChat(@Header("authorization") token: String,
                   @Path("chatId") chatId: Int)
    : Call<Void>
}