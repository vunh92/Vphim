package com.vunh.android.vphim.domain.repository

import com.vunh.android.vphim.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun refreshMovies(page: Int = 1)
    suspend fun toggleFavorite(movieId: String)
}
