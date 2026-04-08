package com.vunh.android.vphim.presentation.ui.screen.home

import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val message: String = "",
    val movies: List<Movie> = emptyList(),
    val categories: List<Category> = emptyList(),
    val actionMovies: List<Movie> = emptyList(),
    val seriesMovies: List<Movie> = emptyList(),
    val singleMovies: List<Movie> = emptyList(),
    val animeMovies: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val user: User? = null
) {
    val isLoggedIn: Boolean get() = user != null
}
