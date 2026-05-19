package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> = repository.getFavoriteMovies()
}
