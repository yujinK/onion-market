package com.yujin.onionmarket.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat (
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("lastMessage")
    var lastMessage: String = "",

    @SerializedName("createdAt")
    var createdAt: String = "",

    @SerializedName("buyUserId")
    var buyUserId: Int = -1,

    @SerializedName("saleId")
    var saleInt: Int = -1
) : Parcelable