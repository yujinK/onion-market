package com.yujin.onionmarket.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CategoryResponse(
    @SerializedName("category")
    var category: List<Category>
)

@Parcelize
data class Category(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("name")
    var name: String = ""
) : Parcelable