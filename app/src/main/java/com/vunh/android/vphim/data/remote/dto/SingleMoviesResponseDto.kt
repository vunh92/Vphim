package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SingleMoviesResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ActionMoviesDataDto
)
