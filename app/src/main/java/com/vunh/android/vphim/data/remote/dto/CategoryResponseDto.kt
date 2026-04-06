package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("items") val items: List<CategoryDto>
)

data class CategoryDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)
