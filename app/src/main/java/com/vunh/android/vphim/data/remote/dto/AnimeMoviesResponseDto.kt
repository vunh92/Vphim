package com.vunh.android.vphim.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeMoviesResponseDto(
    @SerializedName("status") val status: Boolean,
    @SerializedName("msg") val message: String,
    @SerializedName("data") val data: ActionMoviesDataDto
)
