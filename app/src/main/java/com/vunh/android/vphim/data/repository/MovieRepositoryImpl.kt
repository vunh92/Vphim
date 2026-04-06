package com.vunh.android.vphim.data.repository

import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor() : MovieRepository {
    private val movies = MutableStateFlow(
        listOf(
            Movie("1", "Thám tử phố đêm", "Mô tả phim...", "url1", "Hành động"),
            Movie("2", "Lời hứa dưới mưa", "Mô tả phim...", "url2", "Tình cảm"),
            Movie("3", "Hành tinh cuối cùng", "Mô tả phim...", "url3", "Anime")
        )
    )

    override fun getMovies(): Flow<List<Movie>> = movies

    override fun getFavoriteMovies(): Flow<List<Movie>> = movies.map { list ->
        list.filter { it.isFavorite }
    }

    override suspend fun toggleFavorite(movieId: String) {
        val currentList = movies.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == movieId }
        if (index != -1) {
            val movie = currentList[index]
            currentList[index] = movie.copy(isFavorite = !movie.isFavorite)
            movies.value = currentList
        }
    }
}
