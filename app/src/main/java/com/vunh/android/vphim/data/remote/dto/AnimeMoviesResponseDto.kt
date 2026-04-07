package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeMoviesResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ActionMoviesDataDto
)
