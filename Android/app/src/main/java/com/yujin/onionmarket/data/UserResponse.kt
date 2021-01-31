package com.yujin.onionmarket.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class UserResponse(
    @SerializedName("User")
    var user: List<User>,

    @SerializedName("token")
    var token: String
)

@Parcelize
data class User(
    @SerializedName("id")
    @Expose
    var id: Int = 0,

    @SerializedName("email")
    @Expose
    var email: String = "",

    @SerializedName("nick")
    @Expose
    var nick: String = "",

    @SerializedName("img")
    @Expose
    var img: String = "",

    @SerializedName("Location")
    @Expose
    var location: Location
) : Parcelable