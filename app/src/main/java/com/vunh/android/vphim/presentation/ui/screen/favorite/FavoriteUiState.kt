package com.vunh.android.vphim.presentation.ui.screen.favorite

import com.vunh.android.vphim.domain.model.Movie

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Movie> = emptyList(),
    val message: String = ""
)
