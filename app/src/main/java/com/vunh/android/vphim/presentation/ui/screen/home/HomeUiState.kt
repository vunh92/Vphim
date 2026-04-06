package com.vunh.android.vphim.presentation.ui.screen.home

import com.vunh.android.vphim.domain.model.Movie

data class HomeUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
)
