package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("items") val items: List<SearchCountryDto>
)

data class SearchCountryDto(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)
