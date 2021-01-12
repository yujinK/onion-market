package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class ReadSaleResponse(
    @SerializedName("sales")
    var sales: List<Sale>
)

data class Sale(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("title")
    var title: String = "",

    @SerializedName("content")
    var content: String = "",

    @SerializedName("price")
    var price: Int = -1,

    @SerializedName("priceProposal")
    var priceProposal: Boolean = false,

    @SerializedName("chatCount")
    var chatCount: Int = 0,

    @SerializedName("favoriteCount")
    var favoriteCount: Int = 0,

    @SerializedName("viewCount")
    var viewCount: Int = 0,

    @SerializedName("categoryId")
    var categoryId: Int = -1,

    @SerializedName("User")
    var user: User,

    @SerializedName("Images")
    var images: List<Image>
)