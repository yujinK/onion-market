package com.yujin.onionmarket.data

import com.google.gson.annotations.SerializedName

data class FavoriteResponse (
    @SerializedName("favorites")
    var favorites: ArrayList<Favorite>
)

data class Favorite(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("saleId")
    var saleId: Int = -1,

    @SerializedName("userId")
    var userId: Int = -1
)