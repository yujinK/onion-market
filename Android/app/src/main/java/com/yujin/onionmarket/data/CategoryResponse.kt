package com.yujin.onionmarket.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("category")
    var category: List<Category>
)

data class Category(
    @SerializedName("id")
    var id: Int = -1,

    @SerializedName("name")
    var name: String = ""
)