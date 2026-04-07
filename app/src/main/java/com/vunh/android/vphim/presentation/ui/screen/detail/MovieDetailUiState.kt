package com.vunh.android.vphim.presentation.ui.screen.detail

import com.vunh.android.vphim.data.remote.dto.MovieDetailResponseDto

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movieDetail: MovieDetailResponseDto? = null,
    val errorMessage: String? = null
)
