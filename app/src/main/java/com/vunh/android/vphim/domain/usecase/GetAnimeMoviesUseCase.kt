package com.vunh.android.vphim.domain.usecase

import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.domain.repository.MovieRepository
import javax.inject.Inject

class GetAnimeMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): List<Movie> {
        return repository.getAnimeMovies(page, limit)
    }
}
