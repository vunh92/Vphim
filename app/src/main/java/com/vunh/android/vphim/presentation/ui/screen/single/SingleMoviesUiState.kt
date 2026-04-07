package com.vunh.android.vphim.presentation.ui.screen.single

import com.vunh.android.vphim.domain.model.Movie

data class SingleMoviesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null
)
