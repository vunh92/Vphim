package com.vunh.android.vphim.domain.repository

import com.vunh.android.vphim.data.remote.dto.MovieDetailResponseDto
import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun refreshMovies(page: Int = 1)
    suspend fun toggleFavorite(movieId: String)
    suspend fun getCategories(): List<Category>
    suspend fun getMoviesByCategory(categorySlug: String, page: Int = 1, limit: Int = 10): List<Movie>
    suspend fun getSeriesMovies(page: Int = 1, limit: Int = 10): List<Movie>
    suspend fun getSingleMovies(page: Int = 1, limit: Int = 10): List<Movie>
    suspend fun getAnimeMovies(page: Int = 1, limit: Int = 10): List<Movie>
    suspend fun getMovieDetail(slug: String): MovieDetailResponseDto
}
