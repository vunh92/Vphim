package com.vunh.android.vphim.presentation.ui.screen.anime

import com.vunh.android.vphim.domain.model.Movie

data class AnimeMoviesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null
)
