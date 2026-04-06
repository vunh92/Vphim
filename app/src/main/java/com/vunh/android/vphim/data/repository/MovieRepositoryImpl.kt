package com.vunh.android.vphim.data.repository

import com.vunh.android.vphim.data.mapper.toDomain
import com.vunh.android.vphim.data.remote.api.PhimApiService
import com.vunh.android.vphim.domain.model.Category
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val phimApiService: PhimApiService,
) : MovieRepository {
    private val remoteMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val favoriteMovieIds = MutableStateFlow<Set<String>>(emptySet())

    override fun getMovies(): Flow<List<Movie>> {
        return combine(remoteMovies, favoriteMovieIds) { movies, favoriteIds ->
            movies.map { movie ->
                movie.copy(isFavorite = movie.id in favoriteIds)
            }
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> = getMovies().map { list ->
        list.filter { it.isFavorite }
    }

    override suspend fun refreshMovies(page: Int) {
        val response = phimApiService.getLatestMovies(page = page)
        remoteMovies.value = response.items.map { movieDto ->
            movieDto.toDomain()
        }
    }

    override suspend fun toggleFavorite(movieId: String) {
        val currentIds = favoriteMovieIds.value.toMutableSet()
        if (movieId in currentIds) {
            currentIds.remove(movieId)
        } else {
            currentIds.add(movieId)
        }
        favoriteMovieIds.value = currentIds
    }

    override suspend fun getCategories(): List<Category> {
        val response = phimApiService.getCategories()
        return response.items.map { it.toDomain() }
    }
}
